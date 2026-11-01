package com.eshop.service;

import static com.eshop.service.Db.p;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UC05 – Đặt hàng – tính tiền: include UC08 (phí giao), UC09 (thanh toán thẻ), extend UC10 (email).
 */
@Service
public class DatHangService {

    private final Db db;
    private final SanPhamGateway sanPham;
    private final PhiGiaoHangService phiGiao;
    private final TheTinDungValidator validator;
    private final PaymentGateway payment;
    private final EmailService email;
    private final MaSinh maSinh;
    private final Clock clock;

    public DatHangService(Db db, SanPhamGateway sanPham, PhiGiaoHangService phiGiao, TheTinDungValidator validator,
                          PaymentGateway payment, EmailService email, MaSinh maSinh, Clock clock) {
        this.db = db;
        this.sanPham = sanPham;
        this.phiGiao = phiGiao;
        this.validator = validator;
        this.payment = payment;
        this.email = email;
        this.maSinh = maSinh;
        this.clock = clock;
    }

    /** Thông tin người nhận (có thể khác người mua). */
    public record NguoiNhan(String hoTen, String diaChi, String maTinh, String dienThoai) {
    }

    /** Tổng tiền hàng tính theo GIÁ HIỆN HÀNH; từ chối nếu có sản phẩm hết hàng. */
    public BigDecimal tongTienHang(List<MucGioHang> gio) {
        BigDecimal tong = BigDecimal.ZERO;
        for (MucGioHang m : gio) {
            Map<String, Object> sp = sanPham.thongTinSanPham(m.getMaSP());
            if (sp == null || !SanPhamService.conHang(sp)) {
                throw new NghiepVuException("Sản phẩm \"" + m.getTenSP() + "\" hiện đã hết hàng, vui lòng cập nhật giỏ.");
            }
            tong = tong.add(((BigDecimal) sp.get("GiaBan")).multiply(BigDecimal.valueOf(m.getSoLuong())));
        }
        return tong;
    }

    /** Xem trước các khoản tiền trên trang tính tiền. */
    public Map<String, BigDecimal> xemTruoc(List<MucGioHang> gio, String maTinh, String maLoai, String loaiThe) {
        Map<String, BigDecimal> kq = new LinkedHashMap<>();
        BigDecimal hang = tongTienHang(gio);
        BigDecimal phi = (KiemTra.rong(maTinh) || KiemTra.rong(maLoai)) ? BigDecimal.ZERO : phiGiao.tinhPhi(maTinh, maLoai, hang);
        BigDecimal phiThe = KiemTra.rong(loaiThe) ? BigDecimal.ZERO
                : db.money("SELECT PhiGiaoDich FROM LoaiThe WHERE MaLoaiThe = :l", p("l", loaiThe));
        kq.put("TienHang", hang);
        kq.put("PhiGiao", phi);
        kq.put("PhiThe", phiThe);
        kq.put("Tong", hang.add(phi).add(phiThe));
        return kq;
    }

    @Transactional
    public String datHang(String maKH, List<MucGioHang> gio, String maLoaiGiao, NguoiNhan nn, TheTinDung the) {
        if (gio == null || gio.isEmpty()) {
            throw new NghiepVuException("Giỏ hàng đang rỗng.");
        }
        if (nn == null) {
            throw new NghiepVuException("Vui lòng nhập thông tin người nhận.");
        }
        KiemTra.batBuoc("Vui lòng nhập đủ họ tên, địa chỉ, tỉnh/thành và điện thoại người nhận.",
                nn.hoTen(), nn.diaChi(), nn.maTinh(), nn.dienThoai());
        if (!nn.dienThoai().trim().matches("^0\\d{9,10}$")) {
            throw new NghiepVuException("Điện thoại người nhận không hợp lệ.");
        }
        for (MucGioHang m : gio) {
            if (m.getSoLuong() <= 0) {
                throw new NghiepVuException("Số lượng sản phẩm phải lớn hơn 0.");
            }
        }
        Map<String, Object> loaiGiao = phiGiao.loaiGiao(maLoaiGiao);

        // 1. Tiền hàng theo giá hiện hành + phí giao (UC08)
        BigDecimal tienHang = tongTienHang(gio);
        BigDecimal phi = phiGiao.tinhPhi(nn.maTinh(), maLoaiGiao, tienHang);

        // 2. Kiểm tra định dạng thẻ, cộng phí giao dịch của loại thẻ (UC09)
        Map<String, Object> loaiThe = validator.kiemTra(the, YearMonth.now(clock));
        BigDecimal phiThe = (BigDecimal) loaiThe.get("PhiGiaoDich");
        BigDecimal tong = tienHang.add(phi).add(phiThe);

        // 3. Hệ thống thanh toán trực tuyến xác thực thẻ và khả năng thanh toán
        KetQuaThanhToan kq = payment.authorize(the, tong);
        if (!kq.chapNhan()) {
            throw new NghiepVuException("Thanh toán bị từ chối: " + kq.lyDo());
        }

        // 4. Ghi nhận đơn đặt hàng (chỉ lưu 4 số cuối thẻ, không lưu CSV)
        LocalDateTime bayGio = LocalDateTime.now(clock);
        LocalDate duKien = bayGio.toLocalDate().plusDays(((Number) loaiGiao.get("SoNgayXuLy")).intValue());
        String soDon = maSinh.moi("DH");
        db.execute("INSERT INTO DonDatHang(SoDon, MaKH, NgayDat, MaLoaiGiao, DuKienGiao, TenNguoiNhan, DiaChiNhan, MaTinh, DienThoaiNhan, "
                        + "TongTienHang, PhiGiaoHang, MaLoaiThe, PhiThe, SoTheAn, TenChuThe, ThangHetHan, MaGiaoDich, TrangThai) "
                        + "VALUES(:so, :kh, :ngay, :lg, :dk, :tn, :dc, :tinh, :dt, :hang, :phi, :lt, :pt, :an, :chu, :hh, :gd, :tt)",
                p("so", soDon, "kh", maKH, "ngay", bayGio, "lg", maLoaiGiao, "dk", duKien, "tn", nn.hoTen().trim(), "dc", nn.diaChi().trim(),
                        "tinh", nn.maTinh(), "dt", nn.dienThoai().trim(), "hang", tienHang, "phi", phi, "lt", the.loaiThe(), "pt", phiThe,
                        "an", the.soAn(), "chu", the.chuThe().trim(), "hh", the.hetHan().toString(), "gd", kq.maGiaoDich(),
                        "tt", TrangThai.DON_DA_DAT));
        for (MucGioHang m : gio) {
            BigDecimal gia = (BigDecimal) sanPham.thongTinSanPham(m.getMaSP()).get("GiaBan");
            db.execute("INSERT INTO ChiTietDonHang(SoDon, MaSP, SoLuong, DonGia) VALUES(:s, :m, :sl, :g)",
                    p("s", soDon, "m", m.getMaSP(), "sl", m.getSoLuong(), "g", gia));
        }

        // 5. Gửi email xác nhận nếu khách có email (UC10)
        boolean daGui = email.guiXacNhan(soDon);
        return soDon + (daGui ? "|email" : "");
    }
}

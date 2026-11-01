package com.khachsan.qlks.service;

import static com.khachsan.qlks.service.Db.p;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/** UC05–UC08 – Đền bù, hóa đơn, thanh toán, trả phòng (TraPhongService.cs). */
@Service
@Transactional
public class TraPhongService {

    private final Db db;
    private final MaSinh maSinh;

    public TraPhongService(Db db, MaSinh maSinh) {
        this.db = db;
        this.maSinh = maSinh;
    }

    // ------------------------------------------------------------ tra cứu
    @Transactional(readOnly = true)
    public List<Map<String, Object>> layPhieuDangO() {
        return db.query("SELECT d.SoPhieuDat, k.HoTen, d.NgayNhan, d.NgayNhanThucTe, d.NgayTraDuKien FROM PhieuDatPhong d "
                + "JOIN KhachHang k ON d.MaKhach = k.MaKhach WHERE d.TrangThai = :tt ORDER BY d.SoPhieuDat", p("tt", TrangThai.DP_DANG_O));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> layPhongTheoPhieu(String so) {
        return db.query("SELECT c.SoPhong, c.SoNguoi, p.DonGiaNgay FROM ChiTietDatPhong c JOIN Phong p ON c.SoPhong = p.SoPhong "
                + "WHERE c.SoPhieuDat = :s ORDER BY c.SoPhong", p("s", so));
    }

    /** Tiện nghi đã từng lắp đặt vào các phòng của phiếu – để nhân viên phục vụ kiểm tra. */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> layTienNghiTheoPhieu(String so) {
        return db.query("SELECT DISTINCT l.SoPhong, t.MaTienNghi, t.MaLoaiTN, lo.TenLoaiTN, t.TinhTrangHienTai FROM PhieuLapDat l "
                + "JOIN TienNghi t ON l.MaTienNghi = t.MaTienNghi JOIN LoaiTienNghi lo ON t.MaLoaiTN = lo.MaLoaiTN "
                + "WHERE l.SoPhong IN (SELECT SoPhong FROM ChiTietDatPhong WHERE SoPhieuDat = :s) ORDER BY l.SoPhong, t.MaTienNghi", p("s", so));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> layQuyDinh() {
        return db.query("SELECT q.*, l.TenLoaiTN FROM QuyDinhDenBu q JOIN LoaiTienNghi l ON q.MaLoaiTN = l.MaLoaiTN ORDER BY l.TenLoaiTN, q.MucDoThietHai");
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> layDenBu(String so) {
        return db.query("SELECT p.SoPhieuDenBu, p.SoPhong, p.NgayLap, p.TongTien, c.MaTienNghi, c.MucDoThietHai, c.SoTien "
                + "FROM PhieuDenBu p JOIN ChiTietPhieuDenBu c ON p.SoPhieuDenBu = c.SoPhieuDenBu WHERE p.SoPhieuDat = :s "
                + "ORDER BY p.NgayLap, c.MaTienNghi", p("s", so));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> layHoaDon(String soDat) {
        List<Map<String, Object>> l = db.query("SELECT h.*, (SELECT COALESCE(SUM(t.SoTien), 0) FROM ThanhToan t WHERE t.SoHoaDon = h.SoHoaDon) AS DaTra "
                + "FROM HoaDon h WHERE h.SoPhieuDat = :s", p("s", soDat));
        return l.isEmpty() ? null : l.get(0);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> layThanhToan(String soHD) {
        return db.query("SELECT * FROM ThanhToan WHERE SoHoaDon = :h ORDER BY NgayThanhToan", p("h", soHD));
    }

    private void phaiDangO(String soDat) {
        String tt = db.scalar("SELECT TrangThai FROM PhieuDatPhong WHERE SoPhieuDat = :s", p("s", soDat), String.class);
        if (!TrangThai.DP_DANG_O.equals(tt)) {
            throw new NghiepVuException("Phiếu " + soDat + " không ở trạng thái Đang ở.");
        }
    }

    // ------------------------------------------------------------ UC05
    /** BR08: đền bù theo từng tiện nghi và mức độ thiệt hại (mức tiền lấy từ QuyDinhDenBu). */
    public String lapPhieuDenBu(String soDat, String phong, String maNV, List<DenBuItem> ds) {
        if (KiemTra.rong(soDat) || KiemTra.rong(phong) || KiemTra.rong(maNV) || ds == null || ds.isEmpty()) {
            throw new NghiepVuException("Phiếu đền bù chưa đủ thông tin (chọn ít nhất một tiện nghi).");
        }
        phaiDangO(soDat);
        if (db.count("SELECT COUNT(*) FROM ChiTietDatPhong WHERE SoPhieuDat = :s AND SoPhong = :p", p("s", soDat, "p", phong)) == 0) {
            throw new NghiepVuException("Phòng " + phong + " không thuộc phiếu " + soDat + ".");
        }
        Set<String> tn = new HashSet<>();
        List<Object[]> dong = new ArrayList<>();
        BigDecimal tong = BigDecimal.ZERO;
        for (DenBuItem x : ds) {
            if (!tn.add(x.maTienNghi())) {
                throw new NghiepVuException("Tiện nghi " + x.maTienNghi() + " bị chọn hai lần.");
            }
            String loaiTN = db.scalar("SELECT MaLoaiTN FROM TienNghi WHERE MaTienNghi = :m", p("m", x.maTienNghi()), String.class);
            List<Map<String, Object>> qd = db.query("SELECT MaLoaiTN, MucDoThietHai, MucDenBu FROM QuyDinhDenBu WHERE MaQuyDinh = :q",
                    p("q", x.maQuyDinh()));
            if (loaiTN == null || qd.isEmpty()) {
                throw new NghiepVuException("Tiện nghi hoặc quy định đền bù không tồn tại.");
            }
            if (!loaiTN.equals(qd.get(0).get("MaLoaiTN"))) {
                throw new NghiepVuException("Quy định " + x.maQuyDinh() + " không áp dụng cho loại tiện nghi của " + x.maTienNghi() + ".");
            }
            BigDecimal tien = (BigDecimal) qd.get(0).get("MucDenBu");
            tong = tong.add(tien);
            dong.add(new Object[]{x.maTienNghi(), qd.get(0).get("MucDoThietHai"), tien});
        }
        String so = maSinh.moi("DB");
        db.execute("INSERT INTO PhieuDenBu(SoPhieuDenBu, SoPhieuDat, SoPhong, NgayLap, MaNV, TongTien) VALUES(:so, :d, :p, :n, :nv, :t)",
                p("so", so, "d", soDat, "p", phong, "n", LocalDateTime.now(), "nv", maNV, "t", tong));
        for (Object[] r : dong) {
            db.execute("INSERT INTO ChiTietPhieuDenBu(SoPhieuDenBu, MaTienNghi, MucDoThietHai, SoTien) VALUES(:so, :tn, :m, :t)",
                    p("so", so, "tn", r[0], "m", r[1], "t", r[2]));
            db.execute("UPDATE TienNghi SET TinhTrangHienTai = :tt WHERE MaTienNghi = :m", p("tt", r[1], "m", r[0]));
        }
        return "Đã lập phiếu đền bù " + so + ", tổng " + String.format("%,.0f", tong) + " đ.";
    }

    // ------------------------------------------------------------ UC06
    /** BR09: hóa đơn = tiền phòng (Σ đơn giá × số ngày) + tiền dịch vụ. Số ngày do nhân viên xác nhận. */
    public String lapHoaDon(String soDat, String maNV, Integer soNgay) {
        if (KiemTra.rong(soDat) || KiemTra.rong(maNV) || soNgay == null || soNgay <= 0) {
            throw new NghiepVuException("Thông tin hóa đơn chưa hợp lệ (số ngày tính tiền phải > 0).");
        }
        phaiDangO(soDat);
        if (db.count("SELECT COUNT(*) FROM HoaDon WHERE SoPhieuDat = :s", p("s", soDat)) > 0) {
            throw new NghiepVuException("Phiếu " + soDat + " đã có hóa đơn.");
        }
        BigDecimal giaNgay = db.money("SELECT COALESCE(SUM(p.DonGiaNgay), 0) FROM ChiTietDatPhong c JOIN Phong p ON c.SoPhong = p.SoPhong "
                + "WHERE c.SoPhieuDat = :s", p("s", soDat));
        BigDecimal tienPhong = giaNgay.multiply(BigDecimal.valueOf(soNgay));
        BigDecimal tienDV = db.money("SELECT COALESCE(SUM(c.SoLuong * c.DonGia), 0) FROM PhieuSuDungDV h "
                + "JOIN ChiTietPhieuSuDungDV c ON h.SoPhieuSDDV = c.SoPhieuSDDV WHERE h.SoPhieuDat = :s", p("s", soDat));
        String so = maSinh.moi("HD");
        db.execute("INSERT INTO HoaDon(SoHoaDon, SoPhieuDat, NgayLap, MaNV, SoNgayTinhTien, TienPhong, TienDichVu, TrangThai) "
                        + "VALUES(:h, :s, :n, :nv, :ng, :p, :d, :tt)",
                p("h", so, "s", soDat, "n", LocalDateTime.now(), "nv", maNV, "ng", soNgay, "p", tienPhong, "d", tienDV, "tt", TrangThai.HD_CHUA_TT));
        return "Đã lập hóa đơn " + so + ": tiền phòng " + String.format("%,.0f", tienPhong) + " đ + dịch vụ "
                + String.format("%,.0f", tienDV) + " đ.";
    }

    // ------------------------------------------------------------ UC07
    /** BR10: nhiều giao dịch, nhiều hình thức; không vượt số còn phải trả; đủ tiền -> Đã thanh toán. */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String thanhToan(String soHD, String hinhThuc, BigDecimal tien) {
        if (KiemTra.rong(soHD) || KiemTra.rong(hinhThuc) || tien == null || tien.signum() <= 0) {
            throw new NghiepVuException("Thông tin thanh toán không hợp lệ (số tiền phải > 0).");
        }
        if (!TrangThai.HINH_THUC_TT.contains(hinhThuc)) {
            throw new NghiepVuException("Hình thức thanh toán không hợp lệ.");
        }
        BigDecimal tong = db.scalar("SELECT TienPhong + TienDichVu FROM HoaDon WHERE SoHoaDon = :h", p("h", soHD), BigDecimal.class);
        if (tong == null) {
            throw new NghiepVuException("Không tìm thấy hóa đơn.");
        }
        BigDecimal da = db.money("SELECT COALESCE(SUM(SoTien), 0) FROM ThanhToan WHERE SoHoaDon = :h", p("h", soHD));
        BigDecimal sau = da.add(tien);
        if (sau.compareTo(tong) > 0) {
            throw new NghiepVuException("Số tiền thanh toán vượt số còn phải trả (" + String.format("%,.0f", tong.subtract(da)) + " đ).");
        }
        db.execute("INSERT INTO ThanhToan(MaThanhToan, SoHoaDon, NgayThanhToan, HinhThuc, SoTien) VALUES(:m, :h, :n, :ht, :t)",
                p("m", maSinh.moi("TT"), "h", soHD, "n", LocalDateTime.now(), "ht", hinhThuc, "t", tien));
        if (sau.compareTo(tong) == 0) {
            db.execute("UPDATE HoaDon SET TrangThai = :tt WHERE SoHoaDon = :h", p("tt", TrangThai.HD_DA_TT, "h", soHD));
            return "Đã ghi nhận thanh toán bằng " + hinhThuc + ". Hóa đơn đã thanh toán đủ.";
        }
        return "Đã ghi nhận thanh toán bằng " + hinhThuc + ". Còn phải trả " + String.format("%,.0f", tong.subtract(sau)) + " đ.";
    }

    // ------------------------------------------------------------ UC08
    /** Chỉ trả phòng khi hóa đơn đã thanh toán đủ; giải phóng phòng. */
    public String traPhong(String soDat, LocalDateTime ngayTra) {
        Map<String, Object> hd = layHoaDon(soDat);
        if (hd == null) {
            throw new NghiepVuException("Chưa lập hóa đơn cho phiếu đặt phòng.");
        }
        if (!TrangThai.HD_DA_TT.equals(hd.get("TrangThai"))) {
            throw new NghiepVuException("Hóa đơn chưa thanh toán đủ.");
        }
        int n = db.execute("UPDATE PhieuDatPhong SET TrangThai = :moi, NgayTraThucTe = :n WHERE SoPhieuDat = :s AND TrangThai = :cu",
                p("moi", TrangThai.DP_DA_TRA, "n", ngayTra, "s", soDat, "cu", TrangThai.DP_DANG_O));
        if (n == 0) {
            throw new NghiepVuException("Phiếu không ở trạng thái Đang ở.");
        }
        db.execute("UPDATE Phong SET TrangThai = :tt WHERE SoPhong IN (SELECT SoPhong FROM ChiTietDatPhong WHERE SoPhieuDat = :s)",
                p("tt", TrangThai.PHONG_TRONG, "s", soDat));
        return "Đã hoàn tất trả phòng, giải phóng phòng của phiếu " + soDat + ".";
    }
}

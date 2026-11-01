package com.khachsan.qlks.service;

import static com.khachsan.qlks.service.Db.p;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** UC01, UC02, UC11 – Khách hàng, đặt phòng, người lưu trú, nhận phòng, no-show (DatPhongService.cs). */
@Service
@Transactional
public class DatPhongService {

    private final Db db;
    private final MaSinh maSinh;

    public DatPhongService(Db db, MaSinh maSinh) {
        this.db = db;
        this.maSinh = maSinh;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> layKhach() {
        return db.query("SELECT * FROM KhachHang ORDER BY HoTen");
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> layPhieuDat() {
        return db.query("SELECT d.*, k.HoTen FROM PhieuDatPhong d JOIN KhachHang k ON d.MaKhach = k.MaKhach ORDER BY d.NgayLap DESC");
    }

    @Transactional(readOnly = true)
    public Map<String, Object> layPhieu(String so) {
        List<Map<String, Object>> l = db.query("SELECT d.*, k.HoTen, k.SoCMND, k.QuocTich, n.HoTen AS TenLeTan FROM PhieuDatPhong d "
                + "JOIN KhachHang k ON d.MaKhach = k.MaKhach JOIN NhanVien n ON d.MaNVLeTan = n.MaNV WHERE d.SoPhieuDat = :s", p("s", so));
        if (l.isEmpty()) {
            throw new NghiepVuException("Không tìm thấy phiếu đặt " + so + ".");
        }
        return l.get(0);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> layChiTiet(String so) {
        return db.query("SELECT c.*, p.SoNguoiToiDa, p.DonGiaNgay, "
                + "(SELECT COUNT(*) FROM NguoiLuuTru n WHERE n.SoPhieuDat = c.SoPhieuDat AND n.SoPhong = c.SoPhong) AS DaGhi "
                + "FROM ChiTietDatPhong c JOIN Phong p ON c.SoPhong = p.SoPhong WHERE c.SoPhieuDat = :s ORDER BY c.SoPhong", p("s", so));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> layNguoiLuuTru(String so) {
        return db.query("SELECT * FROM NguoiLuuTru WHERE SoPhieuDat = :s ORDER BY SoPhong, MaNguoiLT", p("s", so));
    }

    public String themKhach(String ma, String ten, String cmnd, String qt, String sdt) {
        KiemTra.batBuoc("Thông tin khách chưa đầy đủ (mã, họ tên, CCCD, quốc tịch).", ma, ten, cmnd, qt);
        try {
            db.execute("INSERT INTO KhachHang(MaKhach, HoTen, SoCMND, QuocTich, SoDienThoai) VALUES(:m, :t, :c, :q, :s)",
                    p("m", ma.trim(), "t", ten.trim(), "c", cmnd.trim(), "q", qt.trim(), "s", KiemTra.rong(sdt) ? null : sdt.trim()));
        } catch (DataIntegrityViolationException ex) {
            throw new NghiepVuException("Mã khách hoặc số CCCD đã tồn tại.");
        }
        return "Đã lưu khách hàng " + ten.trim() + ".";
    }

    /** Phòng bị trùng lịch nếu có phiếu Đã đặt / Đang ở chồng lấn khoảng ngày. */
    long demTrungLich(String phong, LocalDate nhan, LocalDate tra) {
        return db.count("SELECT COUNT(*) FROM ChiTietDatPhong c JOIN PhieuDatPhong d ON c.SoPhieuDat = d.SoPhieuDat "
                        + "WHERE c.SoPhong = :p AND d.TrangThai IN (:t1, :t2) AND :nhan <= d.NgayTraDuKien AND :tra >= d.NgayNhan",
                p("p", phong, "t1", TrangThai.DP_DA_DAT, "t2", TrangThai.DP_DANG_O, "nhan", nhan, "tra", tra));
    }

    /** UC01 – Đặt phòng (BR05): kiểm tra sức chứa + trùng lịch cho từng phòng, lưu trong một transaction. */
    public String taoDatPhong(String maKhach, String maNV, LocalDate nhan, LocalDate tra, BigDecimal coc, String kenh,
                              List<PhongDat> ds) {
        if (KiemTra.rong(maKhach) || KiemTra.rong(maNV) || ds == null || ds.isEmpty()) {
            throw new NghiepVuException("Phiếu đặt phòng chưa đủ thông tin (khách, nhân viên, ít nhất một phòng).");
        }
        if (nhan == null || tra == null) {
            throw new NghiepVuException("Vui lòng nhập ngày nhận và ngày trả dự kiến.");
        }
        if (tra.isBefore(nhan)) {
            throw new NghiepVuException("Ngày trả dự kiến không được trước ngày nhận.");
        }
        BigDecimal tienCoc = coc == null ? BigDecimal.ZERO : coc;
        if (tienCoc.signum() < 0) {
            throw new NghiepVuException("Tiền cọc không được âm.");
        }
        if (!TrangThai.KENH_DAT.contains(kenh)) {
            throw new NghiepVuException("Kênh đặt không hợp lệ.");
        }
        Set<String> daChon = new HashSet<>();
        for (PhongDat x : ds) {
            if (!daChon.add(x.soPhong())) {
                throw new NghiepVuException("Phòng " + x.soPhong() + " bị chọn hai lần.");
            }
            List<Map<String, Object>> ph = db.query("SELECT SoNguoiToiDa, TrangThai FROM Phong WHERE SoPhong = :p", p("p", x.soPhong()));
            if (ph.isEmpty()) {
                throw new NghiepVuException("Không tìm thấy phòng " + x.soPhong() + ".");
            }
            int max = ((Number) ph.get(0).get("SoNguoiToiDa")).intValue();
            if (x.soNguoi() <= 0 || x.soNguoi() > max) {
                throw new NghiepVuException("Số người của phòng " + x.soPhong() + " vượt sức chứa (tối đa " + max + ").");
            }
            if (TrangThai.PHONG_BAO_TRI.equals(ph.get(0).get("TrangThai"))) {
                throw new NghiepVuException("Phòng " + x.soPhong() + " đang bảo trì.");
            }
            if (demTrungLich(x.soPhong(), nhan, tra) > 0) {
                throw new NghiepVuException("Phòng " + x.soPhong() + " bị trùng lịch đặt.");
            }
        }
        String so = maSinh.moi("DP");
        db.execute("INSERT INTO PhieuDatPhong(SoPhieuDat, MaKhach, MaNVLeTan, NgayLap, NgayNhan, NgayTraDuKien, TienCoc, KenhDat, TrangThai) "
                        + "VALUES(:s, :k, :nv, :lap, :nhan, :tra, :c, :kenh, :tt)",
                p("s", so, "k", maKhach, "nv", maNV, "lap", LocalDateTime.now(), "nhan", nhan, "tra", tra, "c", tienCoc,
                        "kenh", kenh, "tt", TrangThai.DP_DA_DAT));
        for (PhongDat x : ds) {
            db.execute("INSERT INTO ChiTietDatPhong(SoPhieuDat, SoPhong, SoNguoi) VALUES(:s, :p, :n)",
                    p("s", so, "p", x.soPhong(), "n", x.soNguoi()));
            db.execute("UPDATE Phong SET TrangThai = :tt WHERE SoPhong = :p", p("tt", TrangThai.PHONG_DA_DAT, "p", x.soPhong()));
        }
        return "Đã lập phiếu đặt phòng " + so + ".";
    }

    /** UC02 – BR06: ghi họ tên, CCCD, quốc tịch; không vượt số người đã đăng ký cho phòng. */
    public String themNguoiLuuTru(String so, String phong, String ten, String cmnd, String qt) {
        KiemTra.batBuoc("Thông tin người lưu trú chưa đầy đủ.", so, phong, ten, cmnd, qt);
        String tt = db.scalar("SELECT TrangThai FROM PhieuDatPhong WHERE SoPhieuDat = :s", p("s", so), String.class);
        if (!TrangThai.DP_DA_DAT.equals(tt) && !TrangThai.DP_DANG_O.equals(tt)) {
            throw new NghiepVuException("Chỉ ghi người lưu trú cho phiếu Đã đặt hoặc Đang ở.");
        }
        Integer max = db.scalar("SELECT SoNguoi FROM ChiTietDatPhong WHERE SoPhieuDat = :s AND SoPhong = :p", p("s", so, "p", phong), Integer.class);
        if (max == null) {
            throw new NghiepVuException("Phòng " + phong + " không thuộc phiếu " + so + ".");
        }
        long dem = db.count("SELECT COUNT(*) FROM NguoiLuuTru WHERE SoPhieuDat = :s AND SoPhong = :p", p("s", so, "p", phong));
        if (dem >= max) {
            throw new NghiepVuException("Đã đủ số người đăng ký cho phòng này.");
        }
        db.execute("INSERT INTO NguoiLuuTru(SoPhieuDat, SoPhong, HoTen, SoCMND, QuocTich) VALUES(:s, :p, :t, :c, :q)",
                p("s", so, "p", phong, "t", ten.trim(), "c", cmnd.trim(), "q", qt.trim()));
        return "Đã thêm người lưu trú " + ten.trim() + " vào phòng " + phong + ".";
    }

    /** UC02 – Nhận phòng: phiếu Đã đặt -> Đang ở, phòng -> Đang ở. */
    public String nhanPhong(String so, LocalDateTime thucTe) {
        int n = db.execute("UPDATE PhieuDatPhong SET TrangThai = :moi, NgayNhanThucTe = :t WHERE SoPhieuDat = :s AND TrangThai = :cu",
                p("moi", TrangThai.DP_DANG_O, "t", thucTe, "s", so, "cu", TrangThai.DP_DA_DAT));
        if (n == 0) {
            throw new NghiepVuException("Phiếu không ở trạng thái có thể nhận phòng.");
        }
        db.execute("UPDATE Phong SET TrangThai = :tt WHERE SoPhong IN (SELECT SoPhong FROM ChiTietDatPhong WHERE SoPhieuDat = :s)",
                p("tt", TrangThai.PHONG_DANG_O, "s", so));
        return "Đã nhận phòng cho phiếu " + so + ".";
    }

    /** UC11 – No-show do nhân viên xác nhận (đề không nêu mốc thời gian tự động). */
    public String danhDauNoShow(String so) {
        int n = db.execute("UPDATE PhieuDatPhong SET TrangThai = :moi WHERE SoPhieuDat = :s AND TrangThai = :cu",
                p("moi", TrangThai.DP_NO_SHOW, "s", so, "cu", TrangThai.DP_DA_DAT));
        if (n == 0) {
            throw new NghiepVuException("Chỉ đánh dấu no-show cho phiếu Đã đặt.");
        }
        db.execute("UPDATE Phong SET TrangThai = :tt WHERE SoPhong IN (SELECT SoPhong FROM ChiTietDatPhong WHERE SoPhieuDat = :s)",
                p("tt", TrangThai.PHONG_TRONG, "s", so));
        return "Đã đánh dấu không nhận phòng. Tiền cọc được giữ theo quy định gốc.";
    }
}

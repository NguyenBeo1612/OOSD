package com.khachsan.qlks.service;

import static com.khachsan.qlks.service.Db.p;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** UC10 + UC03 – Phòng, tiện nghi, phiếu lắp đặt / luân chuyển (PhongTienNghiService.cs). */
@Service
@Transactional
public class PhongTienNghiService {

    private final Db db;
    private final MaSinh maSinh;

    public PhongTienNghiService(Db db, MaSinh maSinh) {
        this.db = db;
        this.maSinh = maSinh;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> layPhong() {
        return db.query("SELECT p.*, k.TenKhuVuc FROM Phong p JOIN KhuVuc k ON p.MaKhuVuc = k.MaKhuVuc ORDER BY p.SoPhong");
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> layTienNghi() {
        return db.query("SELECT t.*, l.TenLoaiTN FROM TienNghi t JOIN LoaiTienNghi l ON t.MaLoaiTN = l.MaLoaiTN ORDER BY t.MaTienNghi");
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> layLapDat() {
        return db.query("SELECT p.*, l.TenLoaiTN, n.HoTen FROM PhieuLapDat p JOIN TienNghi t ON p.MaTienNghi = t.MaTienNghi "
                + "JOIN LoaiTienNghi l ON t.MaLoaiTN = l.MaLoaiTN JOIN NhanVien n ON p.MaNV = n.MaNV ORDER BY p.NgayLap DESC, p.SoPhieuLapDat DESC");
    }

    /** BR01, BR02. */
    public String themPhong(String so, String khu, Integer max, BigDecimal gia) {
        KiemTra.batBuoc("Thông tin phòng không hợp lệ.", so, khu);
        if (max == null || max <= 0 || gia == null || gia.signum() < 0) {
            throw new NghiepVuException("Số người tối đa phải > 0 và đơn giá phải >= 0.");
        }
        try {
            db.execute("INSERT INTO Phong(SoPhong, MaKhuVuc, SoNguoiToiDa, DonGiaNgay, TrangThai) VALUES(:s, :k, :m, :g, :tt)",
                    p("s", so.trim(), "k", khu, "m", max, "g", gia, "tt", TrangThai.PHONG_TRONG));
        } catch (DataIntegrityViolationException ex) {
            throw new NghiepVuException("Số phòng đã tồn tại hoặc khu vực không hợp lệ.");
        }
        return "Đã thêm phòng " + so.trim() + ".";
    }

    /** BR03: số thứ tự phân biệt trong cùng loại tiện nghi. */
    public String themTienNghi(String ma, String loai, Integer stt, String tinhTrang) {
        KiemTra.batBuoc("Thông tin tiện nghi không hợp lệ.", ma, loai);
        if (stt == null || stt <= 0) {
            throw new NghiepVuException("Số thứ tự phải > 0.");
        }
        if (db.count("SELECT COUNT(*) FROM TienNghi WHERE MaLoaiTN = :l AND SoThuTu = :s", p("l", loai, "s", stt)) > 0) {
            throw new NghiepVuException("Số thứ tự " + stt + " đã có trong loại tiện nghi này (BR03).");
        }
        try {
            db.execute("INSERT INTO TienNghi(MaTienNghi, MaLoaiTN, SoThuTu, TinhTrangHienTai) VALUES(:m, :l, :s, :t)",
                    p("m", ma.trim(), "l", loai, "s", stt, "t", KiemTra.rong(tinhTrang) ? "Tốt" : tinhTrang.trim()));
        } catch (DataIntegrityViolationException ex) {
            throw new NghiepVuException("Mã tiện nghi đã tồn tại.");
        }
        return "Đã thêm tiện nghi " + ma.trim() + ".";
    }

    /** UC03 – BR04: trong một ngày một thiết bị chỉ được trang bị cho một phòng. */
    public String lapDat(String maTN, String soPhong, LocalDate ngay, String tinhTrang, String maNV, String ghiChu) {
        KiemTra.batBuoc("Phiếu lắp đặt chưa đủ thông tin.", maTN, soPhong, tinhTrang, maNV);
        if (ngay == null) {
            throw new NghiepVuException("Vui lòng chọn ngày lắp đặt.");
        }
        if (db.count("SELECT COUNT(*) FROM PhieuLapDat WHERE MaTienNghi = :m AND NgayLap = :n", p("m", maTN, "n", ngay)) > 0) {
            throw new NghiepVuException("Thiết bị này đã được lắp cho một phòng khác trong ngày đã chọn.");
        }
        String so = maSinh.moi("LD");
        try {
            db.execute("INSERT INTO PhieuLapDat(SoPhieuLapDat, MaTienNghi, SoPhong, NgayLap, TinhTrang, MaNV, GhiChu) "
                            + "VALUES(:so, :tn, :ph, :n, :tt, :nv, :g)",
                    p("so", so, "tn", maTN, "ph", soPhong, "n", ngay, "tt", tinhTrang.trim(), "nv", maNV,
                            "g", KiemTra.rong(ghiChu) ? null : ghiChu.trim()));
        } catch (DataIntegrityViolationException ex) {
            throw new NghiepVuException("Thiết bị này đã được lắp cho một phòng khác trong ngày đã chọn.");
        }
        db.execute("UPDATE TienNghi SET TinhTrangHienTai = :tt WHERE MaTienNghi = :m", p("tt", tinhTrang.trim(), "m", maTN));
        return "Đã lập phiếu lắp đặt " + so + ".";
    }

    /** Chuyển phòng trống sang bảo trì và ngược lại (biểu đồ trạng thái Phong). */
    public String doiTrangThaiBaoTri(String soPhong) {
        String tt = db.scalar("SELECT TrangThai FROM Phong WHERE SoPhong = :s", p("s", soPhong), String.class);
        if (TrangThai.PHONG_TRONG.equals(tt)) {
            db.execute("UPDATE Phong SET TrangThai = :t WHERE SoPhong = :s", p("t", TrangThai.PHONG_BAO_TRI, "s", soPhong));
            return "Phòng " + soPhong + " chuyển sang Bảo trì.";
        }
        if (TrangThai.PHONG_BAO_TRI.equals(tt)) {
            db.execute("UPDATE Phong SET TrangThai = :t WHERE SoPhong = :s", p("t", TrangThai.PHONG_TRONG, "s", soPhong));
            return "Phòng " + soPhong + " hoàn tất bảo trì.";
        }
        throw new NghiepVuException("Chỉ chuyển bảo trì khi phòng đang Trống.");
    }
}

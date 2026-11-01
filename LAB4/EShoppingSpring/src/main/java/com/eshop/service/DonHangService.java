package com.eshop.service;

import static com.eshop.service.Db.p;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** UC11 – Lịch sử đơn của khách; UC12 – Nhân viên quản lý đơn hàng. */
@Service
@Transactional
public class DonHangService {

    private static final String SELECT_DON = "SELECT d.*, k.HoTen AS NguoiMua, k.Email, l.TenLoai, t.TenTinh, lt.TenLoaiThe FROM DonDatHang d "
            + "JOIN KhachHang k ON d.MaKH = k.MaKH JOIN LoaiGiaoHang l ON d.MaLoaiGiao = l.MaLoai "
            + "JOIN TinhThanh t ON d.MaTinh = t.MaTinh JOIN LoaiThe lt ON d.MaLoaiThe = lt.MaLoaiThe ";

    private final Db db;

    public DonHangService(Db db) {
        this.db = db;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> cuaKhach(String maKH) {
        return db.query(SELECT_DON + "WHERE d.MaKH = :k ORDER BY d.NgayDat DESC", p("k", maKH));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> tatCa(String trangThai) {
        if (KiemTra.rong(trangThai)) {
            return db.query(SELECT_DON + "ORDER BY d.NgayDat DESC");
        }
        return db.query(SELECT_DON + "WHERE d.TrangThai = :t ORDER BY d.NgayDat DESC", p("t", trangThai));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> don(String soDon) {
        List<Map<String, Object>> l = db.query(SELECT_DON + "WHERE d.SoDon = :s", p("s", soDon));
        if (l.isEmpty()) {
            throw new NghiepVuException("Không tìm thấy đơn hàng " + soDon + ".");
        }
        return l.get(0);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> chiTiet(String soDon) {
        return db.query("SELECT c.*, s.TenSP, c.SoLuong * c.DonGia AS ThanhTien FROM ChiTietDonHang c JOIN SanPham s ON c.MaSP = s.MaSP "
                + "WHERE c.SoDon = :s ORDER BY s.TenSP", p("s", soDon));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> email(String soDon) {
        return db.query("SELECT * FROM ThuEmail WHERE SoDon = :s ORDER BY ThoiDiemGui", p("s", soDon));
    }

    public String doiTrangThai(String soDon, String moi) {
        String cu = (String) don(soDon).get("TrangThai");
        if (!TrangThai.CHUYEN_HOP_LE.getOrDefault(cu, java.util.Set.of()).contains(moi)) {
            throw new NghiepVuException("Không thể chuyển đơn từ \"" + cu + "\" sang \"" + moi + "\".");
        }
        db.execute("UPDATE DonDatHang SET TrangThai = :m WHERE SoDon = :s AND TrangThai = :c", p("m", moi, "s", soDon, "c", cu));
        return "Đơn " + soDon + " chuyển sang \"" + moi + "\".";
    }
}

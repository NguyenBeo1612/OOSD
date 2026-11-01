package com.khachsan.qlks.service;

import static com.khachsan.qlks.service.Db.p;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** UC04 – Ghi nhận dịch vụ, cộng dồn cùng dịch vụ trong ngày (DichVuService.cs). */
@Service
@Transactional
public class DichVuService {

    private final Db db;
    private final MaSinh maSinh;

    public DichVuService(Db db, MaSinh maSinh) {
        this.db = db;
        this.maSinh = maSinh;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> layPhieuDangO() {
        return db.query("SELECT d.SoPhieuDat, k.HoTen, d.NgayNhan, d.NgayTraDuKien FROM PhieuDatPhong d "
                + "JOIN KhachHang k ON d.MaKhach = k.MaKhach WHERE d.TrangThai = :tt ORDER BY d.SoPhieuDat", p("tt", TrangThai.DP_DANG_O));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> layLichSu(String so) {
        return db.query("SELECT p.SoPhieuSDDV, p.SoPhong, p.NgaySuDung, d.TenDV, c.SoLuong, c.DonGia, c.ThanhTien "
                + "FROM PhieuSuDungDV p JOIN ChiTietPhieuSuDungDV c ON p.SoPhieuSDDV = c.SoPhieuSDDV JOIN DichVu d ON c.MaDV = d.MaDV "
                + "WHERE p.SoPhieuDat = :s ORDER BY p.NgaySuDung, p.SoPhong, d.TenDV", p("s", so));
    }

    /** BR07: cùng dịch vụ, cùng phòng, cùng ngày -> cộng dồn số lượng vào một dòng. */
    public String ghiNhan(String soPhieuDat, String soPhong, LocalDate ngay, String maNV, String maDV, Integer soLuong) {
        if (KiemTra.rong(soPhieuDat) || KiemTra.rong(soPhong) || KiemTra.rong(maNV) || KiemTra.rong(maDV)
                || ngay == null || soLuong == null || soLuong <= 0) {
            throw new NghiepVuException("Thông tin sử dụng dịch vụ không hợp lệ (số lượng phải > 0).");
        }
        String tt = db.scalar("SELECT TrangThai FROM PhieuDatPhong WHERE SoPhieuDat = :s", p("s", soPhieuDat), String.class);
        if (!TrangThai.DP_DANG_O.equals(tt)) {
            throw new NghiepVuException("Chỉ ghi nhận dịch vụ cho phiếu đang lưu trú.");
        }
        if (db.count("SELECT COUNT(*) FROM ChiTietDatPhong WHERE SoPhieuDat = :s AND SoPhong = :p", p("s", soPhieuDat, "p", soPhong)) == 0) {
            throw new NghiepVuException("Phòng " + soPhong + " không thuộc phiếu " + soPhieuDat + ".");
        }
        BigDecimal gia = db.scalar("SELECT DonGia FROM DichVu WHERE MaDV = :d", p("d", maDV), BigDecimal.class);
        if (gia == null) {
            throw new NghiepVuException("Không tìm thấy dịch vụ.");
        }
        String so = db.scalar("SELECT SoPhieuSDDV FROM PhieuSuDungDV WHERE SoPhieuDat = :s AND SoPhong = :p AND NgaySuDung = :n",
                p("s", soPhieuDat, "p", soPhong, "n", ngay), String.class);
        if (so == null) {
            so = maSinh.moi("SD");
            db.execute("INSERT INTO PhieuSuDungDV(SoPhieuSDDV, SoPhieuDat, SoPhong, NgaySuDung, MaNV) VALUES(:so, :s, :p, :n, :nv)",
                    p("so", so, "s", soPhieuDat, "p", soPhong, "n", ngay, "nv", maNV));
        }
        boolean daCo = db.count("SELECT COUNT(*) FROM ChiTietPhieuSuDungDV WHERE SoPhieuSDDV = :so AND MaDV = :d", p("so", so, "d", maDV)) > 0;
        if (daCo) {
            db.execute("UPDATE ChiTietPhieuSuDungDV SET SoLuong = SoLuong + :sl, DonGia = :g WHERE SoPhieuSDDV = :so AND MaDV = :d",
                    p("sl", soLuong, "g", gia, "so", so, "d", maDV));
            return "Đã cộng dồn " + soLuong + " vào dịch vụ đã dùng trong ngày (BR07).";
        }
        db.execute("INSERT INTO ChiTietPhieuSuDungDV(SoPhieuSDDV, MaDV, SoLuong, DonGia) VALUES(:so, :d, :sl, :g)",
                p("so", so, "d", maDV, "sl", soLuong, "g", gia));
        return "Đã ghi nhận dịch vụ vào phiếu " + so + ".";
    }
}

package com.khachsan.qlks.service;

import static com.khachsan.qlks.service.Db.p;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** UC12 – Thống kê tổng hợp và thống kê dịch vụ theo khoảng thời gian (ThongKeService.cs). */
@Service
@Transactional(readOnly = true)
public class ThongKeService {

    private final Db db;

    public ThongKeService(Db db) {
        this.db = db;
    }

    private static void kiemTra(LocalDate tu, LocalDate den) {
        if (tu == null || den == null || den.isBefore(tu)) {
            throw new NghiepVuException("Đến ngày không được trước từ ngày.");
        }
    }

    public Map<String, Object> tongHop(LocalDate tu, LocalDate den) {
        kiemTra(tu, den);
        Map<String, Object> pr = p("tu", tu, "den", den);
        Map<String, Object> kq = new LinkedHashMap<>();
        kq.put("SoPhieuDat", db.count("SELECT COUNT(*) FROM PhieuDatPhong WHERE CAST(NgayLap AS DATE) BETWEEN :tu AND :den", pr));
        kq.put("DangO", db.count("SELECT COUNT(*) FROM PhieuDatPhong WHERE TrangThai = :tt", p("tt", TrangThai.DP_DANG_O)));
        kq.put("SoHoaDon", db.count("SELECT COUNT(*) FROM HoaDon WHERE CAST(NgayLap AS DATE) BETWEEN :tu AND :den", pr));
        kq.put("DoanhThuHoaDon", db.money("SELECT COALESCE(SUM(TienPhong + TienDichVu), 0) FROM HoaDon WHERE CAST(NgayLap AS DATE) BETWEEN :tu AND :den", pr));
        kq.put("TongDenBu", db.money("SELECT COALESCE(SUM(TongTien), 0) FROM PhieuDenBu WHERE CAST(NgayLap AS DATE) BETWEEN :tu AND :den", pr));
        return kq;
    }

    public List<Map<String, Object>> dichVu(LocalDate tu, LocalDate den) {
        kiemTra(tu, den);
        return db.query("SELECT d.MaDV, d.TenDV, SUM(c.SoLuong) AS TongSoLuong, SUM(c.SoLuong * c.DonGia) AS TongTien "
                + "FROM PhieuSuDungDV p JOIN ChiTietPhieuSuDungDV c ON p.SoPhieuSDDV = c.SoPhieuSDDV JOIN DichVu d ON c.MaDV = d.MaDV "
                + "WHERE p.NgaySuDung BETWEEN :tu AND :den GROUP BY d.MaDV, d.TenDV ORDER BY TongTien DESC", p("tu", tu, "den", den));
    }
}

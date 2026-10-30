package com.example.QuanLyCaoOc.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PhieuGiaHanRepository {
    private final JdbcTemplate jdbcTemplate;

    public PhieuGiaHanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void taoPhieuGiaHan(String soPhieuGH, String soHopDong, String maPhong, int thoiGianGiaHan) {
        // 1. Thêm bản ghi vào bảng PhieuGiaHan
        String sqlPhieu = "INSERT INTO PhieuGiaHan (SoPhieuGH, SoHopDong, MaPhong, NgayLapGH, ThoiGianGiaHan, TrangThai) " +
                         "VALUES (?, ?, ?, GETDATE(), ?, N'Đã duyệt')";
        jdbcTemplate.update(sqlPhieu, soPhieuGH, soHopDong, maPhong, thoiGianGiaHan);

        // 2. Cập nhật cộng thêm thời hạn thuê trong bảng HopDongThue
        String sqlUpdateHD = "UPDATE HopDongThue SET ThoiHanThue = ThoiHanThue + ? WHERE SoHopDong = ?";
        jdbcTemplate.update(sqlUpdateHD, thoiGianGiaHan, soHopDong);
    }
}
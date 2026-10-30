package com.example.QuanLyCaoOc.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.QuanLyCaoOc.entity.KhachHang;

@Repository
public class KhachHangRepository {
    private final JdbcTemplate jdbcTemplate;

    public KhachHangRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<KhachHang> khRowMapper = (rs, rowNum) -> {
        KhachHang kh = new KhachHang();
        kh.setMaKH(rs.getString("MaKH"));
        kh.setTenCongTy(rs.getString("TenCongTy"));
        kh.setNguoiDaiDien(rs.getString("NguoiDaiDien"));
        kh.setSoDienThoai(rs.getString("SoDienThoai"));
        kh.setEmail(rs.getString("Email"));
        kh.setMaSoThue(rs.getString("MaSoThue"));
        return kh;
    };

    public List<KhachHang> findAll() {
        String sql = "SELECT * FROM KhachHang";
        return jdbcTemplate.query(sql, khRowMapper);
    }

    public int save(KhachHang kh) {
        String sql = "INSERT INTO KhachHang (MaKH, TenCongTy, NguoiDaiDien, SoDienThoai, Email, MaSoThue) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, kh.getMaKH(), kh.getTenCongTy(), kh.getNguoiDaiDien(), kh.getSoDienThoai(), kh.getEmail(), kh.getMaSoThue());
    }
}
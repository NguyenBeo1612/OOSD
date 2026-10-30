package com.example.QuanLyCaoOc.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.QuanLyCaoOc.entity.Phong;

@Repository
public class PhongRepository {
    private final JdbcTemplate jdbcTemplate;

    public PhongRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Phong> phongRowMapper = (rs, rowNum) -> {
        Phong p = new Phong();
        p.setMaPhong(rs.getString("MaPhong"));
        p.setTenTang(rs.getString("TenTang"));
        p.setDienTich(rs.getBigDecimal("DienTich"));
        p.setHuongPhong(rs.getString("HuongPhong"));
        p.setDonGiaNiemYet(rs.getBigDecimal("DonGiaNiemYet"));
        p.setTrangThai(rs.getString("TrangThai"));
        return p;
    };

    public List<Phong> findAll() {
        String sql = "SELECT p.MaPhong, t.TenTang, p.DienTich, p.HuongPhong, p.DonGiaNiemYet, p.TrangThai " +
                     "FROM Phong p JOIN Tang t ON p.MaTang = t.MaTang";
        return jdbcTemplate.query(sql, phongRowMapper);
    }
}
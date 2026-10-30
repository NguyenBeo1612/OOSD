package com.example.QuanLyCaoOc.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.QuanLyCaoOc.entity.HoaDonDichVu;
import com.example.QuanLyCaoOc.entity.HoaDonThuePhong;

@Repository
public class HoaDonRepository {

    private final JdbcTemplate jdbcTemplate;

    public HoaDonRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper ánh xạ dữ liệu bảng HoaDonThuePhong
    private final RowMapper<HoaDonThuePhong> hoaDonThueRowMapper = (rs, rowNum) -> {
        HoaDonThuePhong hd = new HoaDonThuePhong();
        hd.setSoHDThue(rs.getString("SoHDThue"));
        hd.setSoHopDong(rs.getString("SoHopDong"));
        hd.setThang(rs.getInt("Thang"));
        hd.setNam(rs.getInt("Nam"));
        hd.setSoTienThue(rs.getBigDecimal("SoTienThue"));
        if (rs.getDate("HanThanhToan") != null) {
            hd.setHanThanhToan(rs.getDate("HanThanhToan").toLocalDate());
        }
        hd.setTrangThaiThanhToan(rs.getString("TrangThaiThanhToan"));
        return hd;
    };

    // RowMapper ánh xạ dữ liệu bảng HoaDonDichVu
    private final RowMapper<HoaDonDichVu> hoaDonDVRowMapper = (rs, rowNum) -> {
        HoaDonDichVu dv = new HoaDonDichVu();
        dv.setSoHDDV(rs.getString("SoHDDV"));
        dv.setSoHopDong(rs.getString("SoHopDong"));
        dv.setThang(rs.getInt("Thang"));
        dv.setNam(rs.getInt("Nam"));
        dv.setTienDien(rs.getBigDecimal("TienDien"));
        dv.setTienNuoc(rs.getBigDecimal("TienNuoc"));
        dv.setTienInternet(rs.getBigDecimal("TienInternet"));
        dv.setTongTienDV(rs.getBigDecimal("TongTienDV"));
        dv.setTrangThaiThanhToan(rs.getString("TrangThaiThanhToan"));
        return dv;
    };

    // ================= 1. XỬ LÝ HÓA ĐƠN TIỀN THUÊ PHÒNG =================

    // Lấy danh sách hóa đơn tiền thuê theo Số hợp đồng
    public List<HoaDonThuePhong> findHoaDonThueBySoHopDong(String soHopDong) {
        String sql = "SELECT * FROM HoaDonThuePhong WHERE SoHopDong = ? ORDER BY Nam DESC, Thang DESC";
        return jdbcTemplate.query(sql, hoaDonThueRowMapper, soHopDong);
    }

    // Tạo hóa đơn tiền thuê mới
    public void saveHoaDonThue(HoaDonThuePhong hd) {
        String sql = "INSERT INTO HoaDonThuePhong (SoHDThue, SoHopDong, Thang, Nam, SoTienThue, HanThanhToan, TrangThaiThanhToan) " +
                     "VALUES (?, ?, ?, ?, ?, ?, N'Chưa thanh toán')";
        jdbcTemplate.update(sql, 
                hd.getSoHDThue(), 
                hd.getSoHopDong(), 
                hd.getThang(), 
                hd.getNam(), 
                hd.getSoTienThue(), 
                hd.getHanThanhToan());
    }

    // Cập nhật trạng thái thành "Đã thanh toán" cho hóa đơn tiền thuê
    public void thanhToanHoaDonThue(String soHDThue) {
        String sql = "UPDATE HoaDonThuePhong SET TrangThaiThanhToan = N'Đã thanh toán' WHERE SoHDThue = ?";
        jdbcTemplate.update(sql, soHDThue);
    }

    // ================= 2. XỬ LÝ HÓA ĐƠN DỊCH VỤ =================

    // Lấy danh sách hóa đơn dịch vụ theo Số hợp đồng
    public List<HoaDonDichVu> findHoaDonDVBySoHopDong(String soHopDong) {
        String sql = "SELECT * FROM HoaDonDichVu WHERE SoHopDong = ? ORDER BY Nam DESC, Thang DESC";
        return jdbcTemplate.query(sql, hoaDonDVRowMapper, soHopDong);
    }

    // Tạo hóa đơn dịch vụ mới (Tự động tính Tổng tiền DV = Điện + Nước + Internet)
    public void saveHoaDonDV(HoaDonDichVu dv) {
        BigDecimal tongTien = dv.getTienDien().add(dv.getTienNuoc()).add(dv.getTienInternet());
        String sql = "INSERT INTO HoaDonDichVu (SoHDDV, SoHopDong, Thang, Nam, TienDien, TienNuoc, TienInternet, TongTienDV, TrangThaiThanhToan) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, N'Chưa thanh toán')";
        jdbcTemplate.update(sql, 
                dv.getSoHDDV(), 
                dv.getSoHopDong(), 
                dv.getThang(), 
                dv.getNam(), 
                dv.getTienDien(), 
                dv.getTienNuoc(), 
                dv.getTienInternet(), 
                tongTien);
    }

    // Cập nhật trạng thái thành "Đã thanh toán" cho hóa đơn dịch vụ
    public void thanhToanHoaDonDV(String soHDDV) {
        String sql = "UPDATE HoaDonDichVu SET TrangThaiThanhToan = N'Đã thanh toán' WHERE SoHDDV = ?";
        jdbcTemplate.update(sql, soHDDV);
    }
}
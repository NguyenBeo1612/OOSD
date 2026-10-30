package com.example.QuanLyCaoOc.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.QuanLyCaoOc.entity.HopDongThue;

@Repository
public class HopDongRepository {

    private final JdbcTemplate jdbcTemplate;

    public HopDongRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // RowMapper để ánh xạ dữ liệu từ SQL ResultMap sang Entity HopDongThue
    private final RowMapper<HopDongThue> hopDongRowMapper = (rs, rowNum) -> {
        HopDongThue hd = new HopDongThue();
        hd.setSoHopDong(rs.getString("SoHopDong"));
        hd.setMaKH(rs.getString("MaKH"));
        hd.setTenCongTy(rs.getString("TenCongTy")); // Lấy từ bảng KhachHang JOIN sang
        hd.setMaPhong(rs.getString("MaPhong"));     // Lấy từ bảng ChiTietHopDong JOIN sang
        
        if (rs.getDate("NgayLap") != null) {
            hd.setNgayLap(rs.getDate("NgayLap").toLocalDate());
        }
        if (rs.getDate("NgayBatDau") != null) {
            hd.setNgayBatDau(rs.getDate("NgayBatDau").toLocalDate());
        }
        
        hd.setThoiHanThue(rs.getInt("ThoiHanThue"));
        hd.setDonGiaThue(rs.getBigDecimal("DonGiaThueThucTe"));
        hd.setTongTienThue6Thang(rs.getBigDecimal("TongTienThue6Thang"));
        hd.setTrangThai(rs.getString("TrangThai"));
        return hd;
    };

    // 1. Lấy danh sách toàn bộ hợp đồng (có JOIN để lấy Tên công ty và Mã phòng)
    public List<HopDongThue> findAll() {
        String sql = "SELECT hd.SoHopDong, hd.MaKH, kh.TenCongTy, ct.MaPhong, hd.NgayLap, " +
                     "hd.NgayBatDau, hd.ThoiHanThue, ct.DonGiaThueThucTe, hd.TongTienThue6Thang, hd.TrangThai " +
                     "FROM HopDongThue hd " +
                     "INNER JOIN KhachHang kh ON hd.MaKH = kh.MaKH " +
                     "LEFT JOIN ChiTietHopDong ct ON hd.SoHopDong = ct.SoHopDong " +
                     "ORDER BY hd.NgayLap DESC";
        return jdbcTemplate.query(sql, hopDongRowMapper);
    }

    // 2. Tìm hợp đồng theo Số hợp đồng
    public HopDongThue findById(String soHopDong) {
        String sql = "SELECT hd.SoHopDong, hd.MaKH, kh.TenCongTy, ct.MaPhong, hd.NgayLap, " +
                     "hd.NgayBatDau, hd.ThoiHanThue, ct.DonGiaThueThucTe, hd.TongTienThue6Thang, hd.TrangThai " +
                     "FROM HopDongThue hd " +
                     "INNER JOIN KhachHang kh ON hd.MaKH = kh.MaKH " +
                     "LEFT JOIN ChiTietHopDong ct ON hd.SoHopDong = ct.SoHopDong " +
                     "WHERE hd.SoHopDong = ?";
        List<HopDongThue> list = jdbcTemplate.query(sql, hopDongRowMapper, soHopDong);
        return list.isEmpty() ? null : list.get(0);
    }

    // 3. Lập hợp đồng mới (Dùng Transactional để đảm bảo lưu cả HopDong, ChiTietHopDong và cập nhật trạng thái Phong)
    @Transactional
    public void save(HopDongThue hd) {
        // Step 1: Thêm bản ghi vào bảng HopDongThue
        String sqlHD = "INSERT INTO HopDongThue (SoHopDong, MaKH, NgayBatDau, ThoiHanThue, TongTienThue6Thang, TrangThai) " +
                       "VALUES (?, ?, ?, ?, ?, N'Hiệu lực')";
        jdbcTemplate.update(sqlHD, 
                hd.getSoHopDong(), 
                hd.getMaKH(), 
                hd.getNgayBatDau(), 
                hd.getThoiHanThue(), 
                hd.getTongTienThue6Thang());

        // Step 2: Thêm bản ghi vào bảng ChiTietHopDong
        String sqlCT = "INSERT INTO ChiTietHopDong (SoHopDong, MaPhong, DonGiaThueThucTe) VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlCT, 
                hd.getSoHopDong(), 
                hd.getMaPhong(), 
                hd.getDonGiaThue());

        // Step 3: Cập nhật trạng thái phòng vừa thuê thành 'Đã thuê'
        String sqlUpdatePhong = "UPDATE Phong SET TrangThai = N'Đã thuê' WHERE MaPhong = ?";
        jdbcTemplate.update(sqlUpdatePhong, hd.getMaPhong());
    }

    // 4. Hủy / Đóng hợp đồng
    @Transactional
    public void huyHopDong(String soHopDong, String maPhong) {
        // Cập nhật trạng thái hợp đồng
        String sqlHD = "UPDATE HopDongThue SET TrangThai = N'Hết hiệu lực' WHERE SoHopDong = ?";
        jdbcTemplate.update(sqlHD, soHopDong);

        // Trả phòng về trạng thái 'Trống'
        if (maPhong != null && !maPhong.isEmpty()) {
            String sqlPhong = "UPDATE Phong SET TrangThai = N'Trống' WHERE MaPhong = ?";
            jdbcTemplate.update(sqlPhong, maPhong);
        }
    }
}
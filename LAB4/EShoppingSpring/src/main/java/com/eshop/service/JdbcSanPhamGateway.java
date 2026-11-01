package com.eshop.service;

import static com.eshop.service.Db.p;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Adapter đọc dữ liệu sản phẩm. Trong bản cài đặt này hệ thống quản lý sản phẩm dùng chung CSDL,
 * nên adapter đọc trực tiếp các bảng NhomSanPham / SanPham / HinhAnhSanPham.
 * Khi tích hợp thật có thể thay bằng một lớp gọi REST API mà không đổi các Service khác.
 */
@Component
public class JdbcSanPhamGateway implements SanPhamGateway {

    private final Db db;

    public JdbcSanPhamGateway(Db db) {
        this.db = db;
    }

    @Override
    public List<Map<String, Object>> danhSachNhom() {
        return db.query("SELECT n.MaNhom, n.TenNhom, (SELECT COUNT(*) FROM SanPham s WHERE s.MaNhom = n.MaNhom) AS SoSanPham "
                + "FROM NhomSanPham n ORDER BY n.TenNhom");
    }

    @Override
    public List<Map<String, Object>> danhSachSanPham(String maNhom) {
        return db.query("SELECT s.*, (SELECT MIN(h.DuongDan) FROM HinhAnhSanPham h WHERE h.MaSP = s.MaSP AND h.ThuTu = 1) AS Hinh "
                + "FROM SanPham s WHERE s.MaNhom = :n ORDER BY s.TenSP", p("n", maNhom));
    }

    @Override
    public Map<String, Object> thongTinSanPham(String maSP) {
        List<Map<String, Object>> l = db.query("SELECT s.*, n.TenNhom FROM SanPham s JOIN NhomSanPham n ON s.MaNhom = n.MaNhom WHERE s.MaSP = :m",
                p("m", maSP));
        return l.isEmpty() ? null : l.get(0);
    }

    @Override
    public List<String> hinhAnh(String maSP) {
        return db.query("SELECT DuongDan FROM HinhAnhSanPham WHERE MaSP = :m ORDER BY ThuTu", p("m", maSP)).stream()
                .map(r -> (String) r.get("DuongDan")).toList();
    }
}

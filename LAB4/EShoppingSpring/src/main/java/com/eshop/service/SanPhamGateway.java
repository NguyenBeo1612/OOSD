package com.eshop.service;

import java.util.List;
import java.util.Map;

/**
 * Cổng kết nối tới "Hệ thống quản lý sản phẩm" đã có sẵn của cửa hàng.
 * e-Shopping không tự quản lý sản phẩm mà lấy thông tin qua giao diện này.
 */
public interface SanPhamGateway {

    List<Map<String, Object>> danhSachNhom();

    List<Map<String, Object>> danhSachSanPham(String maNhom);

    /** Thông tin sản phẩm (kèm TenNhom), hoặc null nếu không có. */
    Map<String, Object> thongTinSanPham(String maSP);

    List<String> hinhAnh(String maSP);
}

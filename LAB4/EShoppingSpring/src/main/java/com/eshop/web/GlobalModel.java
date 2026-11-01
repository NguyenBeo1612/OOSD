package com.eshop.web;

import com.eshop.service.GioHang;
import com.eshop.service.SanPhamService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Dữ liệu dùng chung cho thanh menu: số lượng trong giỏ, danh sách nhóm sản phẩm. */
@ControllerAdvice
public class GlobalModel {

    private final GioHang gio;
    private final SanPhamService sanPham;

    public GlobalModel(GioHang gio, SanPhamService sanPham) {
        this.gio = gio;
        this.sanPham = sanPham;
    }

    @ModelAttribute("soLuongGio")
    public int soLuongGio() {
        return gio.getSoLuong();
    }

    @ModelAttribute("dsNhom")
    public List<Map<String, Object>> dsNhom() {
        return sanPham.layNhom();
    }
}

package com.eshop.web;

import com.eshop.service.SanPhamService;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** UC01, UC02 – Trang chủ, danh sách theo nhóm, chi tiết sản phẩm. */
@Controller
public class SanPhamController {

    private final SanPhamService s;

    public SanPhamController(SanPhamService s) {
        this.s = s;
    }

    @GetMapping("/")
    public String trangChu() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/nhom/{maNhom}")
    public String nhom(@PathVariable String maNhom, Model model) {
        model.addAttribute("maNhom", maNhom);
        model.addAttribute("ds", s.layTheoNhom(maNhom));
        return "nhom";
    }

    @GetMapping("/san-pham/{maSP}")
    public String chiTiet(@PathVariable String maSP, Model model) {
        Map<String, Object> sp = s.chiTiet(maSP);
        model.addAttribute("sp", sp);
        model.addAttribute("hinh", s.hinhAnh(maSP));
        model.addAttribute("conHang", SanPhamService.conHang(sp));
        return "sanpham";
    }
}

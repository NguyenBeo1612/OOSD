package com.eshop.web;

import com.eshop.service.GioHang;
import com.eshop.service.GioHangService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** UC03, UC04 – Giỏ hàng. */
@Controller
@RequestMapping("/gio-hang")
public class GioHangController {

    private final GioHang gio;
    private final GioHangService s;

    public GioHangController(GioHang gio, GioHangService s) {
        this.gio = gio;
        this.s = s;
    }

    @GetMapping
    public String xem(Model model) {
        s.capNhatGia(gio);
        model.addAttribute("gio", gio);
        return "giohang";
    }

    /** Thêm từ trang danh sách hoặc trang chi tiết; quay lại đúng trang đang xem. */
    @PostMapping("/them")
    public String them(@RequestParam String maSP, @RequestParam(required = false) Integer soLuong,
                       @RequestHeader(value = "Referer", required = false) String referer, RedirectAttributes ra) {
        String ve = referer != null && referer.contains("/nhom/") ? referer.substring(referer.indexOf("/nhom/"))
                : (referer != null && referer.contains("/san-pham/") ? referer.substring(referer.indexOf("/san-pham/")) : "/gio-hang");
        return Flash.chay(() -> s.them(gio, maSP, soLuong), ra, ve);
    }

    @PostMapping("/cap-nhat")
    public String capNhat(@RequestParam String maSP, @RequestParam(required = false) Integer soLuong, RedirectAttributes ra) {
        return Flash.chay(() -> s.capNhat(gio, maSP, soLuong), ra, "/gio-hang");
    }

    @PostMapping("/xoa")
    public String xoa(@RequestParam String maSP, RedirectAttributes ra) {
        return Flash.chay(() -> s.xoa(gio, maSP), ra, "/gio-hang");
    }
}

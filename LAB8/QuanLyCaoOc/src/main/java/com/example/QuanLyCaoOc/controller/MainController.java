package com.example.QuanLyCaoOc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.QuanLyCaoOc.entity.KhachHang;
import com.example.QuanLyCaoOc.repository.KhachHangRepository;
import com.example.QuanLyCaoOc.repository.PhongRepository;

@Controller
public class MainController {

    private final PhongRepository phongRepository;
    private final KhachHangRepository khachHangRepository;

    public MainController(PhongRepository phongRepository, KhachHangRepository khachHangRepository) {
        this.phongRepository = phongRepository;
        this.khachHangRepository = khachHangRepository;
    }

    // 1. Trang chủ Menu
    @GetMapping("/")
    public String index() {
        return "index";
    }


    // 3. Quản lý khách hàng
    @GetMapping("/khach-hang")
    public String danhSachKhachHang(Model model) {
        model.addAttribute("dsKhachHang", khachHangRepository.findAll());
        model.addAttribute("newKhachHang", new KhachHang());
        return "khachhang";
    }

    @PostMapping("/khach-hang/them")
    public String themKhachHang(@ModelAttribute KhachHang khachHang) {
        khachHangRepository.save(khachHang);
        return "redirect:/khach-hang";
    }
}
package com.example.QuanLyCaoOc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.QuanLyCaoOc.repository.PhongRepository;

@Controller
public class PhongController {

    private final PhongRepository phongRepository;

    public PhongController(PhongRepository phongRepository) {
        this.phongRepository = phongRepository;
    }

    @GetMapping("/phong")
    public String danhSachPhong(Model model) {
        // Gọi hàm findAll() mới trong PhongRepository dùng JdbcTemplate
        model.addAttribute("dsPhong", phongRepository.findAll());
        return "phong";
    }
}
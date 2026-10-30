package com.example.QuanLyCaoOc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.QuanLyCaoOc.repository.HoaDonRepository;
import com.example.QuanLyCaoOc.repository.HopDongRepository;

@Controller
@RequestMapping("/thanh-toan")
public class ThanhToanController {

    private final HopDongRepository hopDongRepository;
    private final HoaDonRepository hoaDonRepository;

    public ThanhToanController(HopDongRepository hopDongRepository, HoaDonRepository hoaDonRepository) {
        this.hopDongRepository = hopDongRepository;
        this.hoaDonRepository = hoaDonRepository;
    }

    @GetMapping
    public String index(@RequestParam(value = "soHopDong", required = false) String soHopDong, Model model) {
        model.addAttribute("dsHopDong", hopDongRepository.findAll());
        model.addAttribute("selectedHopDong", soHopDong);
        if (soHopDong != null && !soHopDong.isEmpty()) {
            // Sửa tên phương thức đúng với HoaDonRepository
            model.addAttribute("dsHoaDon", hoaDonRepository.findHoaDonThueBySoHopDong(soHopDong));
        }
        return "thanhtoan";
    }

    @PostMapping("/xac-nhan")
    public String xacNhanThanhToan(@RequestParam("soHDThue") String soHDThue, @RequestParam("soHopDong") String soHopDong) {
        // Sửa tên phương thức đúng với HoaDonRepository
        hoaDonRepository.thanhToanHoaDonThue(soHDThue);
        return "redirect:/thanh-toan?soHopDong=" + soHopDong;
    }
}
package com.example.QuanLyNhanSu.controller;

import com.example.QuanLyNhanSu.entity.PhongBan;
import com.example.QuanLyNhanSu.repository.PhongBanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/danh-muc")
public class DanhMucController {

    @Autowired
    private PhongBanRepository phongBanRepo;

    @GetMapping
    public String hienThiDanhMuc(Model model) {
        model.addAttribute("dsPhongBan", phongBanRepo.findAll());
        return "danh-muc";
    }

    @PostMapping("/luu-phong-ban")
    public String luuPhongBan(@ModelAttribute PhongBan phongBan) {
        phongBanRepo.save(phongBan);
        return "redirect:/danh-muc";
    }
}
package com.example.QuanLyNhanSu.controller;

import com.example.QuanLyNhanSu.repository.NhanVienRepository;
import com.example.QuanLyNhanSu.repository.PhongBanRepository;
import com.example.QuanLyNhanSu.repository.QuyetDinhKTKLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private NhanVienRepository nhanVienRepo;

    @Autowired
    private PhongBanRepository phongBanRepo;

    @Autowired
    private QuyetDinhKTKLRepository quyetDinhKTKLRepo;

    @GetMapping("/")
    public String index(Model model) {
        // Lấy thống kê nhanh hiển thị trên Dashboard
        model.addAttribute("tongNhanVien", nhanVienRepo.count());
        model.addAttribute("tongPhongBan", phongBanRepo.count());
        model.addAttribute("tongKhenThuong", quyetDinhKTKLRepo.count());
        return "index";
    }
}
package com.example.QuanLyNhanSu.controller;

import com.example.QuanLyNhanSu.entity.QuyetDinhKhenThuongKyLuat;
import com.example.QuanLyNhanSu.repository.NhanVienRepository;
import com.example.QuanLyNhanSu.repository.PhongBanRepository;
import com.example.QuanLyNhanSu.repository.QuyetDinhKTKLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/khen-thuong")
public class KhenThuongController {

    @Autowired
    private NhanVienRepository nhanVienRepo;

    @Autowired
    private PhongBanRepository phongBanRepo;

    @Autowired
    private QuyetDinhKTKLRepository quyetDinhKTKLRepo;

    @GetMapping
    public String hienThiFormKhenThuong(Model model) {
        model.addAttribute("dsNhanVien", nhanVienRepo.findAll());
        model.addAttribute("dsPhongBan", phongBanRepo.findAll());
        model.addAttribute("ktkl", new QuyetDinhKhenThuongKyLuat());
        return "khen-thuong";
    }

    @PostMapping("/luu")
    public String luuKhenThuong(@ModelAttribute QuyetDinhKhenThuongKyLuat ktkl) {
        ktkl.setLoaiQuyetDinh("Khen thưởng");
        quyetDinhKTKLRepo.save(ktkl);
        return "redirect:/khen-thuong?success";
    }
}
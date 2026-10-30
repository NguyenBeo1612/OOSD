package com.example.QuanLyNhanSu.controller;

import com.example.QuanLyNhanSu.entity.NhanVien;
import com.example.QuanLyNhanSu.entity.QuyetDinhTuyenDung;
import com.example.QuanLyNhanSu.repository.NhanVienRepository;
import com.example.QuanLyNhanSu.repository.PhongBanRepository;
import com.example.QuanLyNhanSu.repository.QuyetDinhTuyenDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tuyen-dung")
public class TuyenDungController {

    @Autowired
    private PhongBanRepository phongBanRepo;

    @Autowired
    private NhanVienRepository nhanVienRepo;

    @Autowired
    private QuyetDinhTuyenDungRepository quyetDinhTuyenDungRepo;

    @GetMapping
    public String hienThiFormTuyenDung(Model model) {
        model.addAttribute("dsPhongBan", phongBanRepo.findAll());
        model.addAttribute("nhanVien", new NhanVien());
        model.addAttribute("quyetDinh", new QuyetDinhTuyenDung());
        return "tuyen-dung";
    }

    @PostMapping("/luu")
    public String luuTuyenDung(@ModelAttribute NhanVien nhanVien, 
                               @ModelAttribute QuyetDinhTuyenDung quyetDinh) {
        nhanVien.setTrangThai("Thử việc");
        NhanVien nvDaLuu = nhanVienRepo.save(nhanVien);

        quyetDinh.setNhanVien(nvDaLuu);
        quyetDinhTuyenDungRepo.save(quyetDinh);

        return "redirect:/tuyen-dung?success";
    }
}
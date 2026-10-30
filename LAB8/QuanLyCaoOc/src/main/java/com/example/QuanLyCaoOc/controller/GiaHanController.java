package com.example.QuanLyCaoOc.controller;

import com.example.QuanLyCaoOc.repository.HopDongRepository;
import com.example.QuanLyCaoOc.repository.PhieuGiaHanRepository;
import com.example.QuanLyCaoOc.repository.PhongRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/gia-han")
public class GiaHanController {

    private final HopDongRepository hopDongRepository;
    private final PhongRepository phongRepository;
    private final PhieuGiaHanRepository phieuGiaHanRepository;

    public GiaHanController(HopDongRepository hopDongRepository, PhongRepository phongRepository, PhieuGiaHanRepository phieuGiaHanRepository) {
        this.hopDongRepository = hopDongRepository;
        this.phongRepository = phongRepository;
        this.phieuGiaHanRepository = phieuGiaHanRepository;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("dsHopDong", hopDongRepository.findAll());
        model.addAttribute("dsPhong", phongRepository.findAll());
        return "giahan";
    }

    @PostMapping("/them")
    public String lapPhieuGiaHan(@RequestParam("soHopDong") String soHopDong,
                                 @RequestParam("maPhong") String maPhong,
                                 @RequestParam("thoiGianGiaHan") int thoiGianGiaHan) {
        String soPhieuGH = "GH" + System.currentTimeMillis() % 100000;
        phieuGiaHanRepository.taoPhieuGiaHan(soPhieuGH, soHopDong, maPhong, thoiGianGiaHan);
        return "redirect:/gia-han";
    }
}
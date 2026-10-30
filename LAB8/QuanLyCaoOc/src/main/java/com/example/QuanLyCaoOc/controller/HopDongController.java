package com.example.QuanLyCaoOc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.QuanLyCaoOc.entity.HopDongThue;
import com.example.QuanLyCaoOc.repository.HopDongRepository;
import com.example.QuanLyCaoOc.repository.KhachHangRepository;
import com.example.QuanLyCaoOc.repository.PhongRepository;

@Controller
@RequestMapping("/hop-dong")
public class HopDongController {

    private final HopDongRepository hopDongRepository;
    private final KhachHangRepository khachHangRepository;
    private final PhongRepository phongRepository;

    public HopDongController(HopDongRepository hopDongRepository, 
                             KhachHangRepository khachHangRepository, 
                             PhongRepository phongRepository) {
        this.hopDongRepository = hopDongRepository;
        this.khachHangRepository = khachHangRepository;
        this.phongRepository = phongRepository;
    }

    @GetMapping
    public String index(Model model) {
        // Lấy danh sách hợp đồng đã lập
        model.addAttribute("dsHopDong", hopDongRepository.findAll());
        
        // Lấy danh sách khách hàng để chọn trong dropdown
        model.addAttribute("dsKhachHang", khachHangRepository.findAll());
        
        // Chỉ lấy danh sách các phòng có trạng thái "Trống" để cho thuê
        model.addAttribute("dsPhongTrong", phongRepository.findAll().stream()
                .filter(p -> "Trống".equalsIgnoreCase(p.getTrangThai()))
                .toList());
                
        // Khởi tạo đối tượng form
        model.addAttribute("newHopDong", new HopDongThue());
        return "hopdong";
    }

    @PostMapping("/them")
    public String themHopDong(@ModelAttribute HopDongThue hopDong) {
        // Sửa hàm gọi từ createHopDong sang save để khớp với HopDongRepository
        hopDongRepository.save(hopDong);
        return "redirect:/hop-dong";
    }
}
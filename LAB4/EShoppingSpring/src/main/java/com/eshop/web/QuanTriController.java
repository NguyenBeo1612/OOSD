package com.eshop.web;

import com.eshop.service.DonHangService;
import com.eshop.service.TrangThai;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** UC12 – Nhân viên quản lý đơn đặt hàng. */
@Controller
@RequestMapping("/quan-tri/don-hang")
public class QuanTriController {

    private final DonHangService s;

    public QuanTriController(DonHangService s) {
        this.s = s;
    }

    @GetMapping
    public String ds(@RequestParam(required = false) String trangThai, Model model) {
        model.addAttribute("ds", s.tatCa(trangThai));
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("tatCa", TrangThai.DON_TAT_CA);
        model.addAttribute("chuyen", TrangThai.CHUYEN_HOP_LE);
        return "quantri";
    }

    @GetMapping("/{soDon}")
    public String chiTiet(@PathVariable String soDon, Model model) {
        model.addAttribute("d", s.don(soDon));
        model.addAttribute("ct", s.chiTiet(soDon));
        model.addAttribute("thu", s.email(soDon));
        model.addAttribute("chuyen", TrangThai.CHUYEN_HOP_LE);
        return "quantri-chitiet";
    }

    @PostMapping("/{soDon}")
    public String doi(@PathVariable String soDon, @RequestParam String moi, RedirectAttributes ra) {
        return Flash.chay(() -> s.doiTrangThai(soDon, moi), ra, "/quan-tri/don-hang/" + soDon);
    }
}

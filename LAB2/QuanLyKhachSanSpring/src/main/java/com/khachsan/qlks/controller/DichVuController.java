package com.khachsan.qlks.web;

import static com.khachsan.qlks.web.DanhMucController.chay;

import com.khachsan.qlks.service.DanhMucService;
import com.khachsan.qlks.service.DichVuService;
import com.khachsan.qlks.service.TaiKhoanService;
import com.khachsan.qlks.service.TraPhongService;
import java.security.Principal;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Thay FrmDichVu – UC04. */
@Controller
@RequestMapping("/dich-vu")
public class DichVuController {

    private final DichVuService s;
    private final DanhMucService dm;
    private final TraPhongService tp;
    private final TaiKhoanService tk;

    public DichVuController(DichVuService s, DanhMucService dm, TraPhongService tp, TaiKhoanService tk) {
        this.s = s;
        this.dm = dm;
        this.tp = tp;
        this.tk = tk;
    }

    @GetMapping
    public String trang(@RequestParam(required = false) String soDat, Model model) {
        model.addAttribute("phieu", s.layPhieuDangO());
        model.addAttribute("dichVu", dm.layDichVu());
        model.addAttribute("soDat", soDat);
        model.addAttribute("homNay", LocalDate.now());
        if (soDat != null && !soDat.isBlank()) {
            model.addAttribute("phong", tp.layPhongTheoPhieu(soDat));
            model.addAttribute("lichSu", s.layLichSu(soDat));
        }
        return "dichvu";
    }

    @PostMapping
    public String ghiNhan(@RequestParam String soDat, @RequestParam(required = false) String phong,
                          @RequestParam(required = false) String maDV, @RequestParam(required = false) LocalDate ngay,
                          @RequestParam(required = false) Integer soLuong, Principal principal, RedirectAttributes ra) {
        return chay(() -> s.ghiNhan(soDat, phong, ngay, tk.maNV(principal.getName()), maDV, soLuong), ra, "/dich-vu?soDat=" + soDat);
    }
}

package com.khachsan.qlks.web;

import static com.khachsan.qlks.web.DanhMucController.chay;

import com.khachsan.qlks.service.DanhMucService;
import com.khachsan.qlks.service.PhongTienNghiService;
import com.khachsan.qlks.service.TaiKhoanService;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Thay FrmPhongTienNghi: phòng, tiện nghi, lắp đặt / luân chuyển. */
@Controller
@RequestMapping("/phong")
public class PhongController {

    private final PhongTienNghiService s;
    private final DanhMucService dm;
    private final TaiKhoanService tk;

    public PhongController(PhongTienNghiService s, DanhMucService dm, TaiKhoanService tk) {
        this.s = s;
        this.dm = dm;
        this.tk = tk;
    }

    @GetMapping
    public String trang(Model model) {
        model.addAttribute("phong", s.layPhong());
        model.addAttribute("tienNghi", s.layTienNghi());
        model.addAttribute("lapDat", s.layLapDat());
        model.addAttribute("khuVuc", dm.layKhuVuc());
        model.addAttribute("loaiTN", dm.layLoaiTienNghi());
        model.addAttribute("homNay", LocalDate.now());
        return "phong";
    }

    @PostMapping("/them")
    public String themPhong(@RequestParam String so, @RequestParam String khu, @RequestParam(required = false) Integer max,
                            @RequestParam(required = false) BigDecimal gia, RedirectAttributes ra) {
        return chay(() -> s.themPhong(so, khu, max, gia), ra, "/phong");
    }

    @PostMapping("/bao-tri")
    public String baoTri(@RequestParam String so, RedirectAttributes ra) {
        return chay(() -> s.doiTrangThaiBaoTri(so), ra, "/phong");
    }

    @PostMapping("/tien-nghi")
    public String themTienNghi(@RequestParam String ma, @RequestParam String loai, @RequestParam(required = false) Integer stt,
                               @RequestParam(required = false) String tinhTrang, RedirectAttributes ra) {
        return chay(() -> s.themTienNghi(ma, loai, stt, tinhTrang), ra, "/phong#tien-nghi");
    }

    @PostMapping("/lap-dat")
    public String lapDat(@RequestParam String maTN, @RequestParam String soPhong, @RequestParam(required = false) LocalDate ngay,
                         @RequestParam String tinhTrang, @RequestParam(required = false) String ghiChu, Principal principal,
                         RedirectAttributes ra) {
        return chay(() -> s.lapDat(maTN, soPhong, ngay, tinhTrang, tk.maNV(principal.getName()), ghiChu), ra, "/phong#lap-dat");
    }
}

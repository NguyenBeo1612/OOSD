package com.khachsan.qlks.web;

import com.khachsan.qlks.service.DanhMucService;
import com.khachsan.qlks.service.NghiepVuException;
import com.khachsan.qlks.service.TrangThai;
import java.math.BigDecimal;
import java.util.function.Supplier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Thay FrmDanhMuc (5 tab): khu vực, nhân viên, loại tiện nghi, dịch vụ, quy định đền bù. */
@Controller
@RequestMapping("/danh-muc")
public class DanhMucController {

    private final DanhMucService s;

    public DanhMucController(DanhMucService s) {
        this.s = s;
    }

    @GetMapping
    public String trang(Model model) {
        model.addAttribute("khuVuc", s.layKhuVuc());
        model.addAttribute("nhanVien", s.layNhanVien());
        model.addAttribute("loaiTN", s.layLoaiTienNghi());
        model.addAttribute("dichVu", s.layDichVu());
        model.addAttribute("quyDinh", s.layQuyDinhDenBu());
        model.addAttribute("vaiTros", TrangThai.VAI_TRO_NV);
        return "danhmuc";
    }

    static String chay(Supplier<String> f, RedirectAttributes ra, String ve) {
        try {
            ra.addFlashAttribute("ok", f.get());
        } catch (NghiepVuException ex) {
            ra.addFlashAttribute("loi", ex.getMessage());
        }
        return "redirect:" + ve;
    }

    @PostMapping("/khu-vuc")
    public String khuVuc(@RequestParam String ma, @RequestParam String ten, RedirectAttributes ra) {
        return chay(() -> s.themKhuVuc(ma, ten), ra, "/danh-muc#khu-vuc");
    }

    @PostMapping("/nhan-vien")
    public String nhanVien(@RequestParam String ma, @RequestParam String ten, @RequestParam String vaiTro,
                           @RequestParam(required = false) String sdt, RedirectAttributes ra) {
        return chay(() -> s.themNhanVien(ma, ten, vaiTro, sdt), ra, "/danh-muc#nhan-vien");
    }

    @PostMapping("/loai-tn")
    public String loaiTN(@RequestParam String ma, @RequestParam String ten, RedirectAttributes ra) {
        return chay(() -> s.themLoaiTienNghi(ma, ten), ra, "/danh-muc#loai-tn");
    }

    @PostMapping("/dich-vu")
    public String dichVu(@RequestParam String ma, @RequestParam String ten, @RequestParam String dvt,
                         @RequestParam(required = false) BigDecimal gia, RedirectAttributes ra) {
        return chay(() -> s.themDichVu(ma, ten, dvt, gia), ra, "/danh-muc#dich-vu");
    }

    @PostMapping("/quy-dinh")
    public String quyDinh(@RequestParam String ma, @RequestParam String loai, @RequestParam String mucDo,
                          @RequestParam(required = false) BigDecimal tien, RedirectAttributes ra) {
        return chay(() -> s.themQuyDinh(ma, loai, mucDo, tien), ra, "/danh-muc#quy-dinh");
    }

    @PostMapping("/xoa")
    public String xoa(@RequestParam String loai, @RequestParam String ma, RedirectAttributes ra) {
        return chay(() -> s.xoa(loai, ma), ra, "/danh-muc#" + loai);
    }
}

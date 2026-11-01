package com.thuvien.qltv.web;

import com.thuvien.qltv.domain.NhaXuatBan;
import com.thuvien.qltv.domain.NhanVien;
import com.thuvien.qltv.domain.TheLoai;
import com.thuvien.qltv.service.DanhMucService;
import com.thuvien.qltv.service.NghiepVuException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Thay FrmDanhMuc: 3 trang Nhân viên / Thể loại / Nhà xuất bản. */
@Controller
public class DanhMucController {

    private final DanhMucService service;

    public DanhMucController(DanhMucService service) {
        this.service = service;
    }

    // ---------------- NHÂN VIÊN (Thủ thư) ----------------
    @GetMapping("/nhan-vien")
    public String nhanVien(@RequestParam(required = false) String ma, Model model) {
        if (!model.containsAttribute("nv")) {
            model.addAttribute("nv", ma == null ? new NhanVien() : service.timNhanVien(ma));
            model.addAttribute("capNhat", ma != null);
        }
        model.addAttribute("ds", service.layNhanVien());
        return "nhanvien";
    }

    @PostMapping("/nhan-vien/luu")
    public String luuNhanVien(@ModelAttribute NhanVien nv, @RequestParam(defaultValue = "false") boolean capNhat,
                              RedirectAttributes ra) {
        try {
            ra.addFlashAttribute("ok", service.luuNhanVien(nv, capNhat));
            return "redirect:/nhan-vien";
        } catch (NghiepVuException ex) {
            ra.addFlashAttribute("loi", ex.getMessage());
            ra.addFlashAttribute("nv", nv);
            ra.addFlashAttribute("capNhat", capNhat);
            return "redirect:/nhan-vien";
        }
    }

    @PostMapping("/nhan-vien/xoa")
    public String xoaNhanVien(@RequestParam String ma, RedirectAttributes ra) {
        try {
            ra.addFlashAttribute("ok", service.xoaNhanVien(ma));
        } catch (NghiepVuException ex) {
            ra.addFlashAttribute("loi", ex.getMessage());
        }
        return "redirect:/nhan-vien";
    }

    // ---------------- THỂ LOẠI (NV quản lý sách) ----------------
    @GetMapping("/the-loai")
    public String theLoai(@RequestParam(required = false) String ma, Model model) {
        if (!model.containsAttribute("tl")) {
            model.addAttribute("tl", ma == null ? new TheLoai() : service.timTheLoai(ma));
            model.addAttribute("capNhat", ma != null);
        }
        model.addAttribute("ds", service.layTheLoai());
        return "theloai";
    }

    @PostMapping("/the-loai/luu")
    public String luuTheLoai(@ModelAttribute TheLoai tl, @RequestParam(defaultValue = "false") boolean capNhat,
                             RedirectAttributes ra) {
        try {
            ra.addFlashAttribute("ok", service.luuTheLoai(tl, capNhat));
        } catch (NghiepVuException ex) {
            ra.addFlashAttribute("loi", ex.getMessage());
            ra.addFlashAttribute("tl", tl);
            ra.addFlashAttribute("capNhat", capNhat);
        }
        return "redirect:/the-loai";
    }

    @PostMapping("/the-loai/xoa")
    public String xoaTheLoai(@RequestParam String ma, RedirectAttributes ra) {
        try {
            ra.addFlashAttribute("ok", service.xoaTheLoai(ma));
        } catch (NghiepVuException ex) {
            ra.addFlashAttribute("loi", ex.getMessage());
        }
        return "redirect:/the-loai";
    }

    // ---------------- NHÀ XUẤT BẢN (NV quản lý sách) ----------------
    @GetMapping("/nha-xuat-ban")
    public String nhaXuatBan(@RequestParam(required = false) String ma, Model model) {
        if (!model.containsAttribute("nxb")) {
            model.addAttribute("nxb", ma == null ? new NhaXuatBan() : service.timNhaXuatBan(ma));
            model.addAttribute("capNhat", ma != null);
        }
        model.addAttribute("ds", service.layNhaXuatBan());
        return "nxb";
    }

    @PostMapping("/nha-xuat-ban/luu")
    public String luuNhaXuatBan(@ModelAttribute NhaXuatBan nxb, @RequestParam(defaultValue = "false") boolean capNhat,
                                RedirectAttributes ra) {
        try {
            ra.addFlashAttribute("ok", service.luuNhaXuatBan(nxb, capNhat));
        } catch (NghiepVuException ex) {
            ra.addFlashAttribute("loi", ex.getMessage());
            ra.addFlashAttribute("nxb", nxb);
            ra.addFlashAttribute("capNhat", capNhat);
        }
        return "redirect:/nha-xuat-ban";
    }

    @PostMapping("/nha-xuat-ban/xoa")
    public String xoaNhaXuatBan(@RequestParam String ma, RedirectAttributes ra) {
        try {
            ra.addFlashAttribute("ok", service.xoaNhaXuatBan(ma));
        } catch (NghiepVuException ex) {
            ra.addFlashAttribute("loi", ex.getMessage());
        }
        return "redirect:/nha-xuat-ban";
    }
}

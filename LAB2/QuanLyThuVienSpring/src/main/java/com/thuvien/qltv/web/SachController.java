package com.thuvien.qltv.web;

import com.thuvien.qltv.domain.DauSach;
import com.thuvien.qltv.service.DanhMucService;
import com.thuvien.qltv.service.NghiepVuException;
import com.thuvien.qltv.service.SachService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Thay FrmSach: CRUD + tìm kiếm đầu sách. */
@Controller
@RequestMapping("/sach")
public class SachController {

    private final SachService sachService;
    private final DanhMucService danhMucService;

    public SachController(SachService sachService, DanhMucService danhMucService) {
        this.sachService = sachService;
        this.danhMucService = danhMucService;
    }

    @GetMapping
    public String trang(@RequestParam(required = false) String tuKhoa, @RequestParam(required = false) String ma,
                        Model model) {
        if (!model.containsAttribute("s")) {
            DauSach s = ma == null ? new DauSach() : sachService.tim(ma);
            model.addAttribute("s", s);
            model.addAttribute("capNhat", ma != null);
            model.addAttribute("maTheLoai", s.getTheLoai() == null ? "" : s.getTheLoai().getMaTheLoai());
            model.addAttribute("maNhaXuatBan", s.getNhaXuatBan() == null ? "" : s.getNhaXuatBan().getMaNhaXuatBan());
        }
        model.addAttribute("tuKhoa", tuKhoa);
        model.addAttribute("ds", sachService.layDanhSach(tuKhoa));
        model.addAttribute("theLoais", danhMucService.layTheLoai());
        model.addAttribute("nxbs", danhMucService.layNhaXuatBan());
        return "sach";
    }

    @PostMapping("/luu")
    public String luu(@ModelAttribute("s") DauSach s, @RequestParam(required = false) String maTheLoai,
                      @RequestParam(required = false) String maNhaXuatBan,
                      @RequestParam(defaultValue = "false") boolean capNhat, RedirectAttributes ra) {
        try {
            ra.addFlashAttribute("ok", sachService.luu(s, maTheLoai, maNhaXuatBan, capNhat));
        } catch (NghiepVuException ex) {
            ra.addFlashAttribute("loi", ex.getMessage());
            ra.addFlashAttribute("s", s);
            ra.addFlashAttribute("capNhat", capNhat);
            ra.addFlashAttribute("maTheLoai", maTheLoai);
            ra.addFlashAttribute("maNhaXuatBan", maNhaXuatBan);
        }
        return "redirect:/sach";
    }

    @PostMapping("/xoa")
    public String xoa(@RequestParam String ma, RedirectAttributes ra) {
        try {
            ra.addFlashAttribute("ok", sachService.xoa(ma));
        } catch (NghiepVuException ex) {
            ra.addFlashAttribute("loi", ex.getMessage());
        }
        return "redirect:/sach";
    }
}

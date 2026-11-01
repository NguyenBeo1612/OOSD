package com.thuvien.qltv.web;

import com.thuvien.qltv.domain.DocGia;
import com.thuvien.qltv.domain.TheDocGia;
import com.thuvien.qltv.service.DocGiaService;
import com.thuvien.qltv.service.NghiepVuException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Thay FrmDocGia: quản lý độc giả + cấp / gia hạn thẻ. */
@Controller
@RequestMapping("/doc-gia")
public class DocGiaController {

    private final DocGiaService service;

    public DocGiaController(DocGiaService service) {
        this.service = service;
    }

    @GetMapping
    public String trang(@RequestParam(required = false) String ma, Model model) {
        if (!model.containsAttribute("dg")) {
            model.addAttribute("dg", ma == null ? new DocGia() : service.tim(ma));
            model.addAttribute("capNhat", ma != null);
        }
        Map<String, TheDocGia> theMap = new LinkedHashMap<>();
        for (DocGia d : service.layDanhSach()) {
            service.theHienTai(d.getMaDocGia()).ifPresent(t -> theMap.put(d.getMaDocGia(), t));
        }
        LocalDate homNay = service.homNay();
        model.addAttribute("ds", service.layDanhSach());
        model.addAttribute("theMap", theMap);
        model.addAttribute("homNay", homNay);
        model.addAttribute("maChon", ma);
        model.addAttribute("theChon", ma == null ? null : theMap.get(ma));
        model.addAttribute("hanMacDinh", homNay.plusYears(1));
        return "docgia";
    }

    @PostMapping("/luu")
    public String luu(@ModelAttribute DocGia dg, @RequestParam(defaultValue = "false") boolean capNhat,
                      RedirectAttributes ra) {
        try {
            ra.addFlashAttribute("ok", service.luu(dg, capNhat));
            return "redirect:/doc-gia?ma=" + dg.getMaDocGia();
        } catch (NghiepVuException ex) {
            ra.addFlashAttribute("loi", ex.getMessage());
            ra.addFlashAttribute("dg", dg);
            ra.addFlashAttribute("capNhat", capNhat);
            return "redirect:/doc-gia";
        }
    }

    @PostMapping("/xoa")
    public String xoa(@RequestParam String ma, RedirectAttributes ra) {
        try {
            ra.addFlashAttribute("ok", service.xoa(ma));
        } catch (NghiepVuException ex) {
            ra.addFlashAttribute("loi", ex.getMessage());
        }
        return "redirect:/doc-gia";
    }

    @PostMapping("/cap-the")
    public String capThe(@RequestParam String ma, @RequestParam(required = false) LocalDate ngayCap,
                         @RequestParam(required = false) LocalDate hanSuDung,
                         @RequestParam(defaultValue = "false") boolean daDongLePhi, RedirectAttributes ra) {
        try {
            ra.addFlashAttribute("ok", service.capThe(ma, ngayCap, hanSuDung, daDongLePhi));
        } catch (NghiepVuException ex) {
            ra.addFlashAttribute("loi", ex.getMessage());
        }
        return "redirect:/doc-gia?ma=" + ma;
    }

    @PostMapping("/gia-han")
    public String giaHan(@RequestParam String ma, @RequestParam(required = false) LocalDate hanMoi,
                         @RequestParam(defaultValue = "false") boolean daDongLePhi, RedirectAttributes ra) {
        try {
            ra.addFlashAttribute("ok", service.giaHanThe(ma, hanMoi, daDongLePhi));
        } catch (NghiepVuException ex) {
            ra.addFlashAttribute("loi", ex.getMessage());
        }
        return "redirect:/doc-gia?ma=" + ma;
    }
}

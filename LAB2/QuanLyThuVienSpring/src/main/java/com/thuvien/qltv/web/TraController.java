package com.thuvien.qltv.web;

import com.thuvien.qltv.service.DocGiaService;
import com.thuvien.qltv.service.MuonTraService;
import com.thuvien.qltv.service.NghiepVuException;
import com.thuvien.qltv.service.TaiKhoanService;
import com.thuvien.qltv.service.TinhTrangSach;
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

/** Thay tab "Trả sách" của FrmMuonTra – UC_QLTV_007 + UC_QLTV_008. */
@Controller
@RequestMapping("/tra")
public class TraController {

    private final MuonTraService muonTraService;
    private final DocGiaService docGiaService;
    private final TaiKhoanService taiKhoanService;

    public TraController(MuonTraService muonTraService, DocGiaService docGiaService, TaiKhoanService taiKhoanService) {
        this.muonTraService = muonTraService;
        this.docGiaService = docGiaService;
        this.taiKhoanService = taiKhoanService;
    }

    @GetMapping
    public String trang(@RequestParam(required = false) String maDG, Model model, Principal principal) {
        model.addAttribute("docGias", docGiaService.layDanhSach());
        model.addAttribute("maDG", maDG);
        model.addAttribute("dangMuon", muonTraService.laySachDangMuon(maDG));
        model.addAttribute("tinhTrangs", TinhTrangSach.TAT_CA);
        model.addAttribute("homNay", docGiaService.homNay());
        model.addAttribute("nhanVien", taiKhoanService.nhanVienCua(principal.getName()));
        return "tra";
    }

    @PostMapping
    public String traSach(@RequestParam String maDG, @RequestParam(required = false) String maChiTiet,
                          @RequestParam(required = false) LocalDate ngayTra,
                          @RequestParam(required = false) String tinhTrang,
                          @RequestParam(required = false) BigDecimal phiPhat,
                          Principal principal, RedirectAttributes ra) {
        try {
            String maNV = taiKhoanService.nhanVienCua(principal.getName()).getMaNhanVien();
            ra.addFlashAttribute("ok", muonTraService.traSach(maChiTiet, maNV, ngayTra, tinhTrang, phiPhat));
        } catch (NghiepVuException ex) {
            ra.addFlashAttribute("loi", ex.getMessage());
        }
        return "redirect:/tra?maDG=" + maDG;
    }
}

package com.thuvien.qltv.web;

import com.thuvien.qltv.service.DocGiaService;
import com.thuvien.qltv.service.MuonTraService;
import com.thuvien.qltv.service.NghiepVuException;
import com.thuvien.qltv.service.SachService;
import com.thuvien.qltv.service.TaiKhoanService;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Thay tab "Mượn sách" của FrmMuonTra – UC_QLTV_003 + UC_QLTV_006. */
@Controller
@RequestMapping("/muon")
public class MuonController {

    private final MuonTraService muonTraService;
    private final DocGiaService docGiaService;
    private final SachService sachService;
    private final TaiKhoanService taiKhoanService;

    public MuonController(MuonTraService muonTraService, DocGiaService docGiaService, SachService sachService,
                          TaiKhoanService taiKhoanService) {
        this.muonTraService = muonTraService;
        this.docGiaService = docGiaService;
        this.sachService = sachService;
        this.taiKhoanService = taiKhoanService;
    }

    @GetMapping
    public String trang(@RequestParam(required = false) String maDG, Model model, Principal principal) {
        LocalDate homNay = docGiaService.homNay();
        model.addAttribute("docGias", docGiaService.layDanhSach());
        model.addAttribute("sachCon", sachService.laySachConTrongKho());
        model.addAttribute("maDG", maDG);
        model.addAttribute("ngayMuon", homNay);
        model.addAttribute("ngayHenTra", homNay.plusDays(14));
        model.addAttribute("nhanVien", taiKhoanService.nhanVienCua(principal.getName()));
        if (maDG != null && !maDG.isBlank()) {
            model.addAttribute("the", docGiaService.theHienTai(maDG).orElse(null));
            model.addAttribute("dangMuon", muonTraService.laySachDangMuon(maDG));
            try {
                model.addAttribute("dieuKienOk", muonTraService.kiemTraDieuKienMuon(maDG, 0));
            } catch (NghiepVuException ex) {
                model.addAttribute("dieuKienLoi", ex.getMessage());
            }
        }
        return "muon";
    }

    @PostMapping
    public String lapPhieu(@RequestParam String maDG, @RequestParam(required = false) List<String> maDauSach,
                           @RequestParam(required = false) LocalDate ngayMuon,
                           @RequestParam(required = false) LocalDate ngayHenTra,
                           Principal principal, RedirectAttributes ra) {
        try {
            String maNV = taiKhoanService.nhanVienCua(principal.getName()).getMaNhanVien();
            ra.addFlashAttribute("ok", muonTraService.lapPhieuMuon(maDG, maNV,
                    maDauSach == null ? List.of() : maDauSach, ngayMuon, ngayHenTra));
        } catch (NghiepVuException ex) {
            ra.addFlashAttribute("loi", ex.getMessage());
        }
        return "redirect:/muon?maDG=" + maDG;
    }
}

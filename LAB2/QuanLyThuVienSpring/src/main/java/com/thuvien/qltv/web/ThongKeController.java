package com.thuvien.qltv.web;

import com.thuvien.qltv.service.DocGiaService;
import com.thuvien.qltv.service.ThongKeService;
import com.thuvien.qltv.service.ThongKeTongHop;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Thay FrmThongKe – UC_QLTV_009. */
@Controller
@RequestMapping("/thong-ke")
public class ThongKeController {

    private final ThongKeService thongKeService;
    private final DocGiaService docGiaService;

    public ThongKeController(ThongKeService thongKeService, DocGiaService docGiaService) {
        this.thongKeService = thongKeService;
        this.docGiaService = docGiaService;
    }

    @GetMapping
    public String trang(@RequestParam(required = false) LocalDate tu, @RequestParam(required = false) LocalDate den,
                        Model model) {
        LocalDate homNay = docGiaService.homNay();
        LocalDate tuNgay = tu == null ? homNay.withDayOfMonth(1) : tu;
        LocalDate denNgay = den == null ? homNay : den;
        ThongKeTongHop tk = thongKeService.layTongHop(tuNgay, denNgay);
        model.addAttribute("tk", tk);
        model.addAttribute("phat", thongKeService.layChiTietPhat(tk.tuNgay(), tk.denNgay()));
        return "thongke";
    }
}

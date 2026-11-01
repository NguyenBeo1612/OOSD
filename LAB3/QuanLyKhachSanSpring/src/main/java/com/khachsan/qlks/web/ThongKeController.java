package com.khachsan.qlks.web;

import com.khachsan.qlks.service.NghiepVuException;
import com.khachsan.qlks.service.ThongKeService;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Thay FrmThongKe – UC12. */
@Controller
@RequestMapping("/thong-ke")
public class ThongKeController {

    private final ThongKeService s;

    public ThongKeController(ThongKeService s) {
        this.s = s;
    }

    @GetMapping
    public String trang(@RequestParam(required = false) LocalDate tu, @RequestParam(required = false) LocalDate den, Model model) {
        LocalDate homNay = LocalDate.now();
        LocalDate tuNgay = tu == null ? homNay.withDayOfMonth(1) : tu;
        LocalDate denNgay = den == null ? homNay : den;
        model.addAttribute("tu", tuNgay);
        model.addAttribute("den", denNgay);
        try {
            model.addAttribute("th", s.tongHop(tuNgay, denNgay));
            model.addAttribute("dv", s.dichVu(tuNgay, denNgay));
        } catch (NghiepVuException ex) {
            model.addAttribute("loi", ex.getMessage());
        }
        return "thongke";
    }
}

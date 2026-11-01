package com.khachsan.qlks.web;

import static com.khachsan.qlks.web.DanhMucController.chay;

import com.khachsan.qlks.service.DenBuItem;
import com.khachsan.qlks.service.TaiKhoanService;
import com.khachsan.qlks.service.TraPhongService;
import com.khachsan.qlks.service.TrangThai;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Thay FrmTraPhong – UC05 đền bù, UC06 hóa đơn, UC07 thanh toán, UC08 trả phòng. */
@Controller
@RequestMapping("/tra-phong")
public class TraPhongController {

    private final TraPhongService s;
    private final TaiKhoanService tk;

    public TraPhongController(TraPhongService s, TaiKhoanService tk) {
        this.s = s;
        this.tk = tk;
    }

    @GetMapping
    public String trang(@RequestParam(required = false) String soDat, Model model) {
        List<Map<String, Object>> dangO = s.layPhieuDangO();
        model.addAttribute("phieu", dangO);
        model.addAttribute("soDat", soDat);
        model.addAttribute("hinhThucs", TrangThai.HINH_THUC_TT);
        if (soDat != null && !soDat.isBlank()) {
            model.addAttribute("phong", s.layPhongTheoPhieu(soDat));
            model.addAttribute("tienNghi", s.layTienNghiTheoPhieu(soDat));
            model.addAttribute("quyDinh", s.layQuyDinh());
            model.addAttribute("denBu", s.layDenBu(soDat));
            Map<String, Object> hd = s.layHoaDon(soDat);
            model.addAttribute("hd", hd);
            if (hd != null) {
                model.addAttribute("giaoDich", s.layThanhToan((String) hd.get("SoHoaDon")));
                BigDecimal tong = ((BigDecimal) hd.get("TienPhong")).add((BigDecimal) hd.get("TienDichVu"));
                model.addAttribute("tong", tong);
                model.addAttribute("conLai", tong.subtract(new BigDecimal(hd.get("DaTra").toString())));
            }
            // gợi ý số ngày = số đêm dự kiến (nhân viên xác nhận lại)
            dangO.stream().filter(r -> soDat.equals(r.get("SoPhieuDat"))).findFirst().ifPresent(r -> {
                long n = ChronoUnit.DAYS.between(((java.sql.Date) r.get("NgayNhan")).toLocalDate(),
                        ((java.sql.Date) r.get("NgayTraDuKien")).toLocalDate());
                model.addAttribute("soNgayGoiY", Math.max(1, n));
            });
        }
        return "traphong";
    }

    @PostMapping("/den-bu")
    public String denBu(@RequestParam String soDat, @RequestParam(required = false) String phong,
                        @RequestParam(required = false) List<String> maTN, @RequestParam(required = false) List<String> maQD,
                        Principal principal, RedirectAttributes ra) {
        List<DenBuItem> ds = new ArrayList<>();
        if (maTN != null && maQD != null) {
            for (int i = 0; i < Math.min(maTN.size(), maQD.size()); i++) {
                if (!maTN.get(i).isBlank() && !maQD.get(i).isBlank()) {
                    ds.add(new DenBuItem(maTN.get(i), maQD.get(i)));
                }
            }
        }
        return chay(() -> s.lapPhieuDenBu(soDat, phong, tk.maNV(principal.getName()), ds), ra, "/tra-phong?soDat=" + soDat);
    }

    @PostMapping("/hoa-don")
    public String hoaDon(@RequestParam String soDat, @RequestParam(required = false) Integer soNgay, Principal principal,
                         RedirectAttributes ra) {
        return chay(() -> s.lapHoaDon(soDat, tk.maNV(principal.getName()), soNgay), ra, "/tra-phong?soDat=" + soDat);
    }

    @PostMapping("/thanh-toan")
    public String thanhToan(@RequestParam String soDat, @RequestParam String soHD, @RequestParam String hinhThuc,
                            @RequestParam(required = false) BigDecimal soTien, RedirectAttributes ra) {
        return chay(() -> s.thanhToan(soHD, hinhThuc, soTien), ra, "/tra-phong?soDat=" + soDat);
    }

    @PostMapping("/tra")
    public String tra(@RequestParam String soDat, RedirectAttributes ra) {
        return chay(() -> s.traPhong(soDat, LocalDateTime.now()), ra, "/tra-phong");
    }
}

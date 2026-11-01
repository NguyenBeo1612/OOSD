package com.khachsan.qlks.web;

import static com.khachsan.qlks.web.DanhMucController.chay;

import com.khachsan.qlks.service.DatPhongService;
import com.khachsan.qlks.service.PhongDat;
import com.khachsan.qlks.service.PhongTienNghiService;
import com.khachsan.qlks.service.TaiKhoanService;
import com.khachsan.qlks.service.TrangThai;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Thay FrmDatPhong: khách hàng, đặt phòng, người lưu trú, nhận phòng, no-show. */
@Controller
@RequestMapping("/dat-phong")
public class DatPhongController {

    private final DatPhongService s;
    private final PhongTienNghiService phong;
    private final TaiKhoanService tk;

    public DatPhongController(DatPhongService s, PhongTienNghiService phong, TaiKhoanService tk) {
        this.s = s;
        this.phong = phong;
        this.tk = tk;
    }

    @GetMapping
    public String trang(Model model) {
        LocalDate homNay = LocalDate.now();
        model.addAttribute("khach", s.layKhach());
        model.addAttribute("phong", phong.layPhong());
        model.addAttribute("phieu", s.layPhieuDat());
        model.addAttribute("kenhs", TrangThai.KENH_DAT);
        model.addAttribute("homNay", homNay);
        model.addAttribute("ngayMai", homNay.plusDays(1));
        return "datphong";
    }

    @PostMapping("/khach")
    public String themKhach(@RequestParam String ma, @RequestParam String ten, @RequestParam String cmnd,
                            @RequestParam String qt, @RequestParam(required = false) String sdt, RedirectAttributes ra) {
        return chay(() -> s.themKhach(ma, ten, cmnd, qt, sdt), ra, "/dat-phong");
    }

    /** Mỗi ô số người đặt tên sn_&lt;SoPhong&gt;; phòng có số người &gt; 0 được đưa vào phiếu. */
    @PostMapping
    public String datPhong(@RequestParam String maKhach, @RequestParam String kenh,
                           @RequestParam(required = false) LocalDate nhan, @RequestParam(required = false) LocalDate tra,
                           @RequestParam(required = false) BigDecimal coc, @RequestParam Map<String, String> all,
                           Principal principal, RedirectAttributes ra) {
        List<PhongDat> ds = new ArrayList<>();
        all.forEach((k, v) -> {
            if (k.startsWith("sn_") && v != null && !v.isBlank()) {
                int n = Integer.parseInt(v.trim());
                if (n != 0) {
                    ds.add(new PhongDat(k.substring(3), n));
                }
            }
        });
        return chay(() -> s.taoDatPhong(maKhach, tk.maNV(principal.getName()), nhan, tra, coc, kenh, ds), ra, "/dat-phong");
    }

    @GetMapping("/{so}")
    public String chiTiet(@PathVariable String so, Model model) {
        model.addAttribute("pd", s.layPhieu(so));
        model.addAttribute("ct", s.layChiTiet(so));
        model.addAttribute("nguoi", s.layNguoiLuuTru(so));
        return "datphong-chitiet";
    }

    @PostMapping("/{so}/nguoi-luu-tru")
    public String themNguoi(@PathVariable String so, @RequestParam String phong, @RequestParam String ten,
                            @RequestParam String cmnd, @RequestParam String qt, RedirectAttributes ra) {
        return chay(() -> s.themNguoiLuuTru(so, phong, ten, cmnd, qt), ra, "/dat-phong/" + so);
    }

    @PostMapping("/{so}/nhan-phong")
    public String nhanPhong(@PathVariable String so, RedirectAttributes ra) {
        return chay(() -> s.nhanPhong(so, LocalDateTime.now()), ra, "/dat-phong/" + so);
    }

    @PostMapping("/{so}/no-show")
    public String noShow(@PathVariable String so, RedirectAttributes ra) {
        return chay(() -> s.danhDauNoShow(so), ra, "/dat-phong/" + so);
    }
}

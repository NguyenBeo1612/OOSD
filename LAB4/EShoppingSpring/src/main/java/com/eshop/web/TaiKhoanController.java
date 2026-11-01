package com.eshop.web;

import com.eshop.service.DonHangService;
import com.eshop.service.KhachHangService;
import com.eshop.service.NghiepVuException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** UC07 – Đăng ký; UC11 – Đơn hàng của tôi. */
@Controller
public class TaiKhoanController {

    private final KhachHangService khach;
    private final DonHangService donHang;

    public TaiKhoanController(KhachHangService khach, DonHangService donHang) {
        this.khach = khach;
        this.donHang = donHang;
    }

    @GetMapping("/dang-ky")
    public String dangKyForm() {
        return "dangky";
    }

    @PostMapping("/dang-ky")
    public String dangKy(@RequestParam String hoTen, @RequestParam(required = false) LocalDate ngaySinh, @RequestParam String soCMND,
                         @RequestParam String diaChi, @RequestParam String dienThoai, @RequestParam String tenDangNhap,
                         @RequestParam String matKhau, @RequestParam(required = false) String email, Model model, RedirectAttributes ra) {
        try {
            ra.addFlashAttribute("ok", khach.dangKy(hoTen, ngaySinh, soCMND, diaChi, dienThoai, tenDangNhap, matKhau, email));
            return "redirect:/login";
        } catch (NghiepVuException ex) {
            model.addAttribute("loi", ex.getMessage());
            model.addAttribute("f", Map.of("hoTen", hoTen, "ngaySinh", ngaySinh == null ? "" : ngaySinh.toString(), "soCMND", soCMND,
                    "diaChi", diaChi, "dienThoai", dienThoai, "tenDangNhap", tenDangNhap, "email", email == null ? "" : email));
            return "dangky";
        }
    }

    @GetMapping("/don-hang")
    public String donCuaToi(Principal principal, Model model) {
        String maKH = (String) khach.theoTenDangNhap(principal.getName()).get("MaKH");
        model.addAttribute("ds", donHang.cuaKhach(maKH));
        return "donhang";
    }

    @GetMapping("/don-hang/{soDon}")
    public String chiTiet(@PathVariable String soDon, Principal principal, Model model) {
        Map<String, Object> don = donHang.don(soDon);
        String maKH = (String) khach.theoTenDangNhap(principal.getName()).get("MaKH");
        if (!maKH.equals(don.get("MaKH"))) {
            throw new NghiepVuException("Bạn không có quyền xem đơn hàng này.");
        }
        model.addAttribute("d", don);
        model.addAttribute("ct", donHang.chiTiet(soDon));
        model.addAttribute("thu", donHang.email(soDon));
        return "donhang-chitiet";
    }
}

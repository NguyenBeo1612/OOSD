package com.eshop.web;

import com.eshop.service.DatHangService;
import com.eshop.service.GioHang;
import com.eshop.service.KhachHangService;
import com.eshop.service.NghiepVuException;
import com.eshop.service.PhiGiaoHangService;
import com.eshop.service.TheTinDung;
import java.security.Principal;
import java.time.YearMonth;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** UC05 – Đặt hàng – tính tiền (yêu cầu đăng nhập vai trò khách hàng). */
@Controller
@RequestMapping("/dat-hang")
public class DatHangController {

    private final GioHang gio;
    private final DatHangService datHang;
    private final PhiGiaoHangService phiGiao;
    private final KhachHangService khach;
    private final com.eshop.service.Db db;

    public DatHangController(GioHang gio, DatHangService datHang, PhiGiaoHangService phiGiao, KhachHangService khach,
                             com.eshop.service.Db db) {
        this.gio = gio;
        this.datHang = datHang;
        this.phiGiao = phiGiao;
        this.khach = khach;
        this.db = db;
    }

    /** Trang tính tiền; các tham số tùy chọn dùng để xem trước phí khi khách đổi lựa chọn. */
    @GetMapping
    public String form(@RequestParam(required = false) String maLoai, @RequestParam(required = false) String maTinh,
                       @RequestParam(required = false) String loaiThe, Model model, Principal principal, RedirectAttributes ra) {
        if (gio.isRong()) {
            ra.addFlashAttribute("loi", "Giỏ hàng đang rỗng.");
            return "redirect:/gio-hang";
        }
        Map<String, Object> kh = khach.theoTenDangNhap(principal.getName());
        model.addAttribute("kh", kh);
        model.addAttribute("gio", gio);
        model.addAttribute("loaiGiao", phiGiao.layLoaiGiao());
        model.addAttribute("tinh", phiGiao.layTinhThanh());
        model.addAttribute("loaiThes", db.query("SELECT * FROM LoaiThe ORDER BY MaLoaiThe"));
        model.addAttribute("maLoai", maLoai == null ? "THUONG" : maLoai);
        model.addAttribute("maTinh", maTinh == null ? "HCM" : maTinh);
        model.addAttribute("loaiThe", loaiThe == null ? "VISA" : loaiThe);
        model.addAttribute("thangNay", YearMonth.now().toString());
        try {
            model.addAttribute("tien", datHang.xemTruoc(gio.getDanhSach(), maTinh == null ? "HCM" : maTinh,
                    maLoai == null ? "THUONG" : maLoai, loaiThe == null ? "VISA" : loaiThe));
        } catch (NghiepVuException ex) {
            model.addAttribute("loi", ex.getMessage());
        }
        return "dathang";
    }

    @PostMapping
    public String datHang(@RequestParam String maLoai, @RequestParam String tenNguoiNhan, @RequestParam String diaChiNhan,
                          @RequestParam String maTinh, @RequestParam String dienThoaiNhan, @RequestParam String loaiThe,
                          @RequestParam String soThe, @RequestParam(required = false) String hetHan, @RequestParam String chuThe,
                          @RequestParam String csv, Principal principal, RedirectAttributes ra) {
        try {
            YearMonth hh = (hetHan == null || hetHan.isBlank()) ? null : YearMonth.parse(hetHan);
            String maKH = (String) khach.theoTenDangNhap(principal.getName()).get("MaKH");
            String kq = datHang.datHang(maKH, gio.getDanhSach(), maLoai,
                    new DatHangService.NguoiNhan(tenNguoiNhan, diaChiNhan, maTinh, dienThoaiNhan),
                    new TheTinDung(loaiThe, soThe, hh, chuThe, csv));
            gio.lamRong();
            String soDon = kq.split("\\|")[0];
            ra.addFlashAttribute("ok", "Đặt hàng thành công! Mã đơn " + soDon
                    + (kq.endsWith("|email") ? ". Email xác nhận đã được gửi." : "."));
            return "redirect:/don-hang/" + soDon;
        } catch (NghiepVuException ex) {
            ra.addFlashAttribute("loi", ex.getMessage());
            return "redirect:/dat-hang?maLoai=" + maLoai + "&maTinh=" + maTinh + "&loaiThe=" + loaiThe;
        }
    }
}

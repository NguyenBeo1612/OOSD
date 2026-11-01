package com.eshop;

import static com.eshop.service.Db.p;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.eshop.service.DatHangService;
import com.eshop.service.Db;
import com.eshop.service.DonHangService;
import com.eshop.service.GioHang;
import com.eshop.service.GioHangService;
import com.eshop.service.KhachHangService;
import com.eshop.service.MockPaymentGateway;
import com.eshop.service.NghiepVuException;
import com.eshop.service.PhiGiaoHangService;
import com.eshop.service.SanPhamService;
import com.eshop.service.TheTinDung;
import com.eshop.service.TrangThai;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/** 17 test case nghiệp vụ e-Shopping (TC01–TC17). Chạy trên H2 (profile demo), mỗi test tự rollback: mvn test */
@SpringBootTest
@ActiveProfiles("demo")
@Transactional
class NghiepVuTest {

    private static final String KH = "KH0001";
    private static final YearMonth CON_HAN = YearMonth.now().plusYears(2);

    @Autowired Db db;
    @Autowired SanPhamService sanPhamService;
    @Autowired GioHangService gioHangService;
    @Autowired KhachHangService khachHangService;
    @Autowired PhiGiaoHangService phiGiaoService;
    @Autowired DatHangService datHangService;
    @Autowired DonHangService donHangService;

    private GioHang gio(String maSP, int sl) {
        GioHang g = new GioHang();
        gioHangService.them(g, maSP, sl);
        return g;
    }

    private static TheTinDung visa() {
        return new TheTinDung("VISA", "4111 1111 1111 1111", CON_HAN, "NGUYEN MINH KHANG", "123");
    }

    private static DatHangService.NguoiNhan nguoiNhan(String tinh) {
        return new DatHangService.NguoiNhan("Trần Thị Mai", "45 Hai Bà Trưng", tinh, "0912345678");
    }

    @Test
    @DisplayName("TC01 – Xem danh sách sản phẩm theo nhóm")
    void tc01() {
        List<Map<String, Object>> ds = sanPhamService.layTheoNhom("DOCHOI");
        assertEquals(3, ds.size());
        assertTrue(ds.stream().allMatch(r -> "DOCHOI".equals(r.get("MaNhom"))));
    }

    @Test
    @DisplayName("TC02 – Thêm sản phẩm hết hàng vào giỏ → từ chối")
    void tc02() {
        NghiepVuException ex = assertThrows(NghiepVuException.class, () -> gio("SP005", 1));
        assertTrue(ex.getMessage().contains("hết hàng"));
    }

    @Test
    @DisplayName("TC03 – Thêm cùng sản phẩm 2 lần cộng dồn; cập nhật SL = 0 → loại khỏi giỏ")
    void tc03() {
        GioHang g = gio("SP004", 1);
        gioHangService.them(g, "SP004", 2);
        assertEquals(3, g.getSoLuong());
        assertEquals(0, new BigDecimal("750000").compareTo(g.getTongTien()));
        gioHangService.capNhat(g, "SP004", 0);
        assertTrue(g.isRong());
    }

    @Test
    @DisplayName("TC04 – Đăng ký trùng tên đăng nhập → từ chối")
    void tc04() {
        NghiepVuException ex = assertThrows(NghiepVuException.class, () -> khachHangService.dangKy("Lê Văn A",
                LocalDate.of(1999, 1, 1), "079099999999", "Q3, TP.HCM", "0901234567", "khang", "abcdef", null));
        assertTrue(ex.getMessage().contains("Tên đăng nhập"));
    }

    @Test
    @DisplayName("TC05 – Đăng ký hợp lệ → lưu khách hàng, mật khẩu đã mã hóa, email không bắt buộc")
    void tc05() {
        khachHangService.dangKy("Lê Văn A", LocalDate.of(1999, 1, 1), "079099999999", "Q3, TP.HCM", "0901234567", "levana", "abcdef", "");
        Map<String, Object> kh = khachHangService.theoTenDangNhap("levana");
        assertTrue(((String) kh.get("MatKhau")).startsWith("{bcrypt}"));
        assertEquals(null, kh.get("Email"));
    }

    @Test
    @DisplayName("TC06 – Đơn 500.000 đ, phiếu thường, giao Hà Nội → phí theo bảng khu vực (40.000 đ)")
    void tc06() {
        assertEquals(0, new BigDecimal("40000").compareTo(phiGiaoService.tinhPhi("HN", "THUONG", new BigDecimal("500000"))));
        assertEquals(0, new BigDecimal("15000").compareTo(phiGiaoService.tinhPhi("HCM", "THUONG", new BigDecimal("500000"))));
    }

    @Test
    @DisplayName("TC07 – Đơn 1.200.000 đ: chuyển phát nhanh miễn phí, giao trong ngày vẫn tính phí")
    void tc07() {
        BigDecimal tong = new BigDecimal("1200000");
        assertEquals(0, BigDecimal.ZERO.compareTo(phiGiaoService.tinhPhi("DN", "CPN", tong)));
        assertEquals(0, new BigDecimal("200000").compareTo(phiGiaoService.tinhPhi("DN", "CPN_NGAY", tong)));
    }

    @Test
    @DisplayName("TC08 – Đơn ≥ 5.000.000 đ: chuyển phát nhanh trong ngày miễn phí")
    void tc08() {
        assertEquals(0, BigDecimal.ZERO.compareTo(phiGiaoService.tinhPhi("HN", "CPN_NGAY", new BigDecimal("5000000"))));
    }

    @Test
    @DisplayName("TC09 – Thẻ VISA chỉ có 15 chữ số → từ chối")
    void tc09() {
        TheTinDung the = new TheTinDung("VISA", "411111111111111", CON_HAN, "A", "123");
        NghiepVuException ex = assertThrows(NghiepVuException.class,
                () -> datHangService.datHang(KH, gio("SP003", 1).getDanhSach(), "THUONG", nguoiNhan("HCM"), the));
        assertTrue(ex.getMessage().contains("16 chữ số"));
    }

    @Test
    @DisplayName("TC10 – Thẻ American Express với CSV 3 chữ số → từ chối (AmEx cần 4)")
    void tc10() {
        TheTinDung the = new TheTinDung("AMEX", "378282246310005", CON_HAN, "A", "123");
        NghiepVuException ex = assertThrows(NghiepVuException.class,
                () -> datHangService.datHang(KH, gio("SP003", 1).getDanhSach(), "THUONG", nguoiNhan("HCM"), the));
        assertTrue(ex.getMessage().contains("4 chữ số"));
    }

    @Test
    @DisplayName("TC11 – Thẻ đã hết hạn → từ chối")
    void tc11() {
        TheTinDung the = new TheTinDung("VISA", "4111111111111111", YearMonth.now().minusMonths(1), "A", "123");
        NghiepVuException ex = assertThrows(NghiepVuException.class,
                () -> datHangService.datHang(KH, gio("SP003", 1).getDanhSach(), "THUONG", nguoiNhan("HCM"), the));
        assertTrue(ex.getMessage().contains("hết hạn"));
    }

    @Test
    @DisplayName("TC12 – Số thẻ sai số kiểm tra (Luhn) → từ chối")
    void tc12() {
        TheTinDung the = new TheTinDung("VISA", "4111111111111112", CON_HAN, "A", "123");
        assertThrows(NghiepVuException.class,
                () -> datHangService.datHang(KH, gio("SP003", 1).getDanhSach(), "THUONG", nguoiNhan("HCM"), the));
    }

    @Test
    @DisplayName("TC13 – Hệ thống thanh toán từ chối thẻ → không ghi nhận đơn")
    void tc13() {
        long truoc = db.count("SELECT COUNT(*) FROM DonDatHang", p());
        TheTinDung the = new TheTinDung("VISA", MockPaymentGateway.THE_BI_TU_CHOI, CON_HAN, "A", "123");
        NghiepVuException ex = assertThrows(NghiepVuException.class,
                () -> datHangService.datHang(KH, gio("SP003", 1).getDanhSach(), "THUONG", nguoiNhan("HCM"), the));
        assertTrue(ex.getMessage().contains("từ chối"));
        assertEquals(truoc, db.count("SELECT COUNT(*) FROM DonDatHang", p()));
    }

    @Test
    @DisplayName("TC14 – Đặt hàng hợp lệ: lưu đơn, đơn giá hiện hành, tổng = hàng + phí giao + phí thẻ, chỉ lưu 4 số cuối")
    void tc14() {
        GioHang g = gio("SP003", 1);                                                   // giỏ ghi giá 890.000
        db.execute("UPDATE SanPham SET GiaBan = 950000 WHERE MaSP = 'SP003'", p());   // giá thay đổi trước khi đặt
        String soDon = datHangService.datHang(KH, g.getDanhSach(), "CPN", nguoiNhan("CT"), visa()).split("\\|")[0];
        Map<String, Object> d = donHangService.don(soDon);
        assertEquals(0, new BigDecimal("950000").compareTo((BigDecimal) d.get("TongTienHang")));
        assertEquals(0, new BigDecimal("50000").compareTo((BigDecimal) d.get("PhiGiaoHang")));      // CPN miền Nam, chưa đủ 1 tr
        assertEquals(0, new BigDecimal("5000").compareTo((BigDecimal) d.get("PhiThe")));            // phí VISA
        assertEquals(0, new BigDecimal("1005000").compareTo((BigDecimal) d.get("TongThanhToan")));
        assertEquals("**** **** **** 1111", d.get("SoTheAn"));
        assertEquals(TrangThai.DON_DA_DAT, d.get("TrangThai"));
        assertEquals("Trần Thị Mai", d.get("TenNguoiNhan"));                                     // người nhận khác người mua
        assertEquals(0, new BigDecimal("950000").compareTo((BigDecimal) donHangService.chiTiet(soDon).get(0).get("DonGia")));
    }

    @Test
    @DisplayName("TC15 – Khách có email → gửi email xác nhận không chứa thông tin thẻ; không email → không gửi")
    void tc15() {
        String kq = datHangService.datHang(KH, gio("SP006", 1).getDanhSach(), "THUONG", nguoiNhan("HCM"), visa());
        assertTrue(kq.endsWith("|email"));
        String noiDung = (String) donHangService.email(kq.split("\\|")[0]).get(0).get("NoiDung");
        assertFalse(noiDung.contains("VISA"));                  // không có loại thẻ
        assertFalse(noiDung.contains("****"));                  // không có số thẻ (kể cả dạng che)
        assertFalse(noiDung.contains("NGUYEN MINH KHANG"));     // không có tên chủ thẻ

        khachHangService.dangKy("Không Email", LocalDate.of(1995, 5, 5), "079011112222", "Q1", "0901112222", "khongemail", "abcdef", null);
        String maKH = (String) khachHangService.theoTenDangNhap("khongemail").get("MaKH");
        String kq2 = datHangService.datHang(maKH, gio("SP006", 1).getDanhSach(), "THUONG", nguoiNhan("HCM"), visa());
        assertFalse(kq2.endsWith("|email"));
    }

    @Test
    @DisplayName("TC16 – Giỏ hàng rỗng → không cho đặt hàng")
    void tc16() {
        assertThrows(NghiepVuException.class,
                () -> datHangService.datHang(KH, new GioHang().getDanhSach(), "THUONG", nguoiNhan("HCM"), visa()));
    }

    @Test
    @DisplayName("TC17 – Nhân viên chuyển trạng thái đơn: hợp lệ thì cập nhật, sai luồng thì từ chối")
    void tc17() {
        String soDon = datHangService.datHang(KH, gio("SP008", 1).getDanhSach(), "THUONG", nguoiNhan("HCM"), visa()).split("\\|")[0];
        donHangService.doiTrangThai(soDon, TrangThai.DON_DANG_XU_LY);
        assertThrows(NghiepVuException.class, () -> donHangService.doiTrangThai(soDon, TrangThai.DON_HOAN_TAT));
        donHangService.doiTrangThai(soDon, TrangThai.DON_DANG_GIAO);
        donHangService.doiTrangThai(soDon, TrangThai.DON_HOAN_TAT);
        assertThrows(NghiepVuException.class, () -> donHangService.doiTrangThai(soDon, TrangThai.DON_HUY));
    }
}

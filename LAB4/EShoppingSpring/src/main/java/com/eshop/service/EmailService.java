package com.eshop.service;

import static com.eshop.service.Db.p;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * UC10 – Gửi email xác nhận đặt hàng (nếu khách có email), KHÔNG kèm thông tin thẻ tín dụng.
 * Bản cài đặt lưu thư vào bảng ThuEmail và ghi log; khi triển khai thật chỉ cần thêm
 * spring-boot-starter-mail và gọi JavaMailSender tại phương thức gui().
 */
@Service
public class EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);
    private final Db db;

    public EmailService(Db db) {
        this.db = db;
    }

    /** @return true nếu đã gửi, false nếu khách không có email. */
    public boolean guiXacNhan(String soDon) {
        Map<String, Object> don = db.query("SELECT d.*, k.HoTen, k.Email, l.TenLoai, t.TenTinh FROM DonDatHang d "
                + "JOIN KhachHang k ON d.MaKH = k.MaKH JOIN LoaiGiaoHang l ON d.MaLoaiGiao = l.MaLoai "
                + "JOIN TinhThanh t ON d.MaTinh = t.MaTinh WHERE d.SoDon = :s", p("s", soDon)).get(0);
        String email = (String) don.get("Email");
        if (KiemTra.rong(email)) {
            return false;
        }
        List<Map<String, Object>> ct = db.query("SELECT c.SoLuong, c.DonGia, s.TenSP FROM ChiTietDonHang c JOIN SanPham s ON c.MaSP = s.MaSP "
                + "WHERE c.SoDon = :s ORDER BY s.TenSP", p("s", soDon));
        StringBuilder nd = new StringBuilder();
        nd.append("Kính gửi ").append(don.get("HoTen")).append(",\n\n")
          .append("e-Shopping xác nhận đơn đặt hàng ").append(soDon).append(" đã được ghi nhận lúc ").append(don.get("NgayDat")).append(".\n\n")
          .append("Sản phẩm:\n");
        for (Map<String, Object> r : ct) {
            nd.append(" - ").append(r.get("TenSP")).append(" x ").append(r.get("SoLuong"))
              .append(" @ ").append(String.format("%,.0f", r.get("DonGia"))).append(" đ\n");
        }
        nd.append("\nTiền hàng: ").append(String.format("%,.0f", don.get("TongTienHang"))).append(" đ")
          .append("\nPhí giao hàng (").append(don.get("TenLoai")).append("): ").append(String.format("%,.0f", don.get("PhiGiaoHang"))).append(" đ")
          .append("\nPhí thanh toán: ").append(String.format("%,.0f", don.get("PhiThe"))).append(" đ")
          .append("\nTổng thanh toán: ").append(String.format("%,.0f", don.get("TongThanhToan"))).append(" đ")
          .append("\n\nNgười nhận: ").append(don.get("TenNguoiNhan")).append(" – ").append(don.get("DienThoaiNhan"))
          .append("\nĐịa chỉ: ").append(don.get("DiaChiNhan")).append(", ").append(don.get("TenTinh"))
          .append("\nDự kiến giao: ").append(don.get("DuKienGiao"))
          .append("\n\nCảm ơn quý khách đã mua sắm tại e-Shopping!");
        gui(soDon, email, "[e-Shopping] Xác nhận đơn hàng " + soDon, nd.toString());
        return true;
    }

    private void gui(String soDon, String den, String tieuDe, String noiDung) {
        db.execute("INSERT INTO ThuEmail(SoDon, DenEmail, TieuDe, NoiDung, ThoiDiemGui) VALUES(:s, :d, :t, :n, :g)",
                p("s", soDon, "d", den, "t", tieuDe, "n", noiDung, "g", LocalDateTime.now()));
        LOG.info("Đã gửi email xác nhận {} tới {}", soDon, den);
    }
}

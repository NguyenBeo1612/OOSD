package com.eshop.service;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Mô phỏng hệ thống thanh toán để chạy thử (công ty sẽ thay bằng SDK / API của nhà cung cấp đã đăng ký).
 * Quy ước thử nghiệm: thẻ 4000 0000 0000 0002 luôn bị từ chối (không đủ khả năng thanh toán);
 * giao dịch trên 50.000.000 đ bị từ chối vì vượt hạn mức.
 */
@Component
public class MockPaymentGateway implements PaymentGateway {

    public static final String THE_BI_TU_CHOI = "4000000000000002";
    private static final BigDecimal HAN_MUC = new BigDecimal("50000000");

    @Override
    public KetQuaThanhToan authorize(TheTinDung the, BigDecimal soTien) {
        if (THE_BI_TU_CHOI.equals(the.soSach())) {
            return new KetQuaThanhToan(false, null, "Thẻ không đủ khả năng thanh toán.");
        }
        if (soTien.compareTo(HAN_MUC) > 0) {
            return new KetQuaThanhToan(false, null, "Giao dịch vượt hạn mức của thẻ.");
        }
        return new KetQuaThanhToan(true, "GD" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase(), null);
    }
}

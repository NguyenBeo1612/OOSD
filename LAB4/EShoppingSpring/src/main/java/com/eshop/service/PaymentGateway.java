package com.eshop.service;

import java.math.BigDecimal;

/** Giao diện tới Hệ thống dịch vụ thanh toán trực tuyến (actor ngoài hệ thống). */
public interface PaymentGateway {

    /** Kiểm tra tính hợp lệ của thẻ và khả năng thanh toán số tiền; giữ (authorize) số tiền nếu được chấp nhận. */
    KetQuaThanhToan authorize(TheTinDung the, BigDecimal soTien);
}

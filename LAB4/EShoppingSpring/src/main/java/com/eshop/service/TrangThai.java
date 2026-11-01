package com.eshop.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** Giá trị cố định khớp với CHECK constraint trong SQL Server. */
public final class TrangThai {

    public static final String CON_HANG = "Còn hàng";
    public static final String HET_HANG = "Hết hàng";

    public static final String DON_DA_DAT = "Đã đặt";
    public static final String DON_DANG_XU_LY = "Đang xử lý";
    public static final String DON_DANG_GIAO = "Đang giao";
    public static final String DON_HOAN_TAT = "Hoàn tất";
    public static final String DON_HUY = "Hủy";
    public static final List<String> DON_TAT_CA = List.of(DON_DA_DAT, DON_DANG_XU_LY, DON_DANG_GIAO, DON_HOAN_TAT, DON_HUY);

    /** Chuyển trạng thái hợp lệ theo biểu đồ trạng thái DonDatHang. */
    public static final Map<String, Set<String>> CHUYEN_HOP_LE = Map.of(
            DON_DA_DAT, Set.of(DON_DANG_XU_LY, DON_HUY),
            DON_DANG_XU_LY, Set.of(DON_DANG_GIAO, DON_HUY),
            DON_DANG_GIAO, Set.of(DON_HOAN_TAT),
            DON_HOAN_TAT, Set.of(),
            DON_HUY, Set.of());

    private TrangThai() {
    }
}

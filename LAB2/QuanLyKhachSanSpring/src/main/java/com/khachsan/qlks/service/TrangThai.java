package com.khachsan.qlks.service;

import java.util.List;

/** Các giá trị trạng thái / danh mục cố định khớp với CHECK constraint trong SQL Server. */
public final class TrangThai {

    // Phong.TrangThai
    public static final String PHONG_TRONG = "Trống";
    public static final String PHONG_DA_DAT = "Đã đặt";
    public static final String PHONG_DANG_O = "Đang ở";
    public static final String PHONG_BAO_TRI = "Bảo trì";
    // PhieuDatPhong.TrangThai
    public static final String DP_DA_DAT = "Đã đặt";
    public static final String DP_DANG_O = "Đang ở";
    public static final String DP_DA_TRA = "Đã trả";
    public static final String DP_NO_SHOW = "No-show";
    // HoaDon.TrangThai
    public static final String HD_CHUA_TT = "Chưa thanh toán";
    public static final String HD_DA_TT = "Đã thanh toán";

    public static final List<String> KENH_DAT = List.of("Điện thoại", "Website", "Trực tiếp");
    public static final List<String> HINH_THUC_TT = List.of("Tiền mặt", "Chuyển khoản", "Thẻ", "Ví điện tử");   // BR10
    public static final List<String> VAI_TRO_NV = List.of("Lễ tân", "Phục vụ phòng", "Thanh toán", "Quản lý");

    private TrangThai() {
    }
}

package com.thuvien.qltv.service;

import java.util.List;

/** Các giá trị tình trạng sách khi trả (cboTinhTrang của bản WinForms). */
public final class TinhTrangSach {

    public static final String BINH_THUONG = "Bình thường";
    public static final String RACH_HU = "Rách/Hư hỏng";
    public static final String MAT = "Mất";
    public static final List<String> TAT_CA = List.of(BINH_THUONG, RACH_HU, MAT);

    private TinhTrangSach() {
    }
}

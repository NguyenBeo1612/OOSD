package com.thuvien.qltv.service;

/** Tiện ích kiểm tra dữ liệu nhập. */
final class KiemTra {

    private KiemTra() {
    }

    static boolean rong(String s) {
        return s == null || s.trim().isEmpty();
    }

    static String sach(String s) {
        return s == null ? null : s.trim();
    }

    static void batBuoc(String thongBao, String... giaTri) {
        for (String g : giaTri) {
            if (rong(g)) {
                throw new NghiepVuException(thongBao);
            }
        }
    }
}

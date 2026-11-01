package com.eshop.service;

final class KiemTra {

    private KiemTra() {
    }

    static boolean rong(String s) {
        return s == null || s.trim().isEmpty();
    }

    static void batBuoc(String thongBao, String... giaTri) {
        for (String g : giaTri) {
            if (rong(g)) {
                throw new NghiepVuException(thongBao);
            }
        }
    }
}

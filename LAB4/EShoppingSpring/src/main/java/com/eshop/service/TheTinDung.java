package com.eshop.service;

import java.time.YearMonth;

/** Thông tin thẻ khách nhập – chỉ tồn tại trong bộ nhớ khi xử lý, không lưu số thẻ đầy đủ và CSV vào CSDL. */
public record TheTinDung(String loaiThe, String soThe, YearMonth hetHan, String chuThe, String csv) {

    public String soSach() {
        return soThe == null ? "" : soThe.replaceAll("[\\s-]", "");
    }

    /** Dạng che: **** **** **** 1234. */
    public String soAn() {
        String s = soSach();
        return "**** **** **** " + (s.length() >= 4 ? s.substring(s.length() - 4) : s);
    }

    @Override
    public String toString() {      // không in số thẻ / CSV ra log
        return "TheTinDung[" + loaiThe + ", " + soAn() + "]";
    }
}

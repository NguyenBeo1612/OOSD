package com.thuvien.qltv.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

/** Sinh mã phiếu theo thời gian, ví dụ PM20261101193512345 01 (giống bản C#, thêm số đếm chống trùng). */
@Component
public class MaSinh {

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private int dem = 0;

    public synchronized String moi(String tienTo) {
        dem = (dem + 1) % 100;
        return tienTo + LocalDateTime.now().format(F) + String.format("%02d", dem);
    }
}

package com.khachsan.qlks.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

/** Sinh số phiếu theo thời gian (giống "SD" + yyyyMMddHHmmssfff của bản C#), thêm bộ đếm chống trùng. */
@Component
public class MaSinh {

    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private int dem = 0;

    public synchronized String moi(String tienTo) {
        dem = (dem + 1) % 100;
        return tienTo + LocalDateTime.now().format(F) + String.format("%02d", dem);
    }
}

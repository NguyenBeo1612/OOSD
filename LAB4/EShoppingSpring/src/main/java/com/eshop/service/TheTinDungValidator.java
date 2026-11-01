package com.eshop.service;

import static com.eshop.service.Db.p;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Kiểm tra định dạng thẻ trước khi gửi tới hệ thống thanh toán:
 * Visa / Master / Discover 16 chữ số, CSV 3 chữ số; American Express 15 chữ số, CSV 4 chữ số;
 * chưa hết hạn; đúng thuật toán Luhn (chống gõ nhầm).
 */
@Component
public class TheTinDungValidator {

    private final Db db;

    public TheTinDungValidator(Db db) {
        this.db = db;
    }

    /** Trả về thông tin loại thẻ (SoChuSo, SoChuSoCSV, PhiGiaoDich) nếu hợp lệ. */
    public Map<String, Object> kiemTra(TheTinDung the, YearMonth thangHienTai) {
        if (the == null || KiemTra.rong(the.loaiThe())) {
            throw new NghiepVuException("Vui lòng chọn loại thẻ.");
        }
        List<Map<String, Object>> l = db.query("SELECT * FROM LoaiThe WHERE MaLoaiThe = :l", p("l", the.loaiThe()));
        if (l.isEmpty()) {
            throw new NghiepVuException("Loại thẻ không được hỗ trợ.");
        }
        Map<String, Object> loai = l.get(0);
        int soChuSo = ((Number) loai.get("SoChuSo")).intValue();
        int soCSV = ((Number) loai.get("SoChuSoCSV")).intValue();
        String so = the.soSach();
        if (!so.matches("\\d{" + soChuSo + "}")) {
            throw new NghiepVuException("Số thẻ " + loai.get("TenLoaiThe") + " phải gồm " + soChuSo + " chữ số.");
        }
        if (the.csv() == null || !the.csv().matches("\\d{" + soCSV + "}")) {
            throw new NghiepVuException("Mã CSV của thẻ " + loai.get("TenLoaiThe") + " phải gồm " + soCSV + " chữ số.");
        }
        if (the.hetHan() == null || the.hetHan().isBefore(thangHienTai)) {
            throw new NghiepVuException("Thẻ đã hết hạn sử dụng.");
        }
        if (KiemTra.rong(the.chuThe())) {
            throw new NghiepVuException("Vui lòng nhập họ tên chủ thẻ.");
        }
        if (!luhn(so)) {
            throw new NghiepVuException("Số thẻ không hợp lệ (sai số kiểm tra).");
        }
        return loai;
    }

    static boolean luhn(String so) {
        int tong = 0;
        boolean nhanDoi = false;
        for (int i = so.length() - 1; i >= 0; i--) {
            int n = so.charAt(i) - '0';
            if (nhanDoi) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            tong += n;
            nhanDoi = !nhanDoi;
        }
        return tong % 10 == 0;
    }
}

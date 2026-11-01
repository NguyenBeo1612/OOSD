package com.eshop.service;

import static com.eshop.service.Db.p;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * UC08 – Tính phí giao hàng theo khu vực (tỉnh/thành của người nhận) và loại phiếu đặt hàng.
 * Tổng hàng ≥ 1.000.000 đ: miễn phí chuyển phát nhanh (và phiếu thường);
 * tổng hàng ≥ 5.000.000 đ: miễn phí chuyển phát nhanh trong ngày (ngưỡng lưu ở LoaiGiaoHang.NguongMienPhi).
 */
@Service
public class PhiGiaoHangService {

    private final Db db;

    public PhiGiaoHangService(Db db) {
        this.db = db;
    }

    public List<Map<String, Object>> layLoaiGiao() {
        return db.query("SELECT * FROM LoaiGiaoHang ORDER BY SoNgayXuLy DESC");
    }

    public List<Map<String, Object>> layTinhThanh() {
        return db.query("SELECT t.MaTinh, t.TenTinh, k.TenKV FROM TinhThanh t JOIN KhuVucGiao k ON t.MaKV = k.MaKV ORDER BY t.TenTinh");
    }

    public Map<String, Object> loaiGiao(String maLoai) {
        List<Map<String, Object>> l = db.query("SELECT * FROM LoaiGiaoHang WHERE MaLoai = :l", p("l", maLoai));
        if (l.isEmpty()) {
            throw new NghiepVuException("Loại phiếu đặt hàng không hợp lệ.");
        }
        return l.get(0);
    }

    public BigDecimal tinhPhi(String maTinh, String maLoai, BigDecimal tongHang) {
        if (KiemTra.rong(maTinh) || KiemTra.rong(maLoai)) {
            throw new NghiepVuException("Vui lòng chọn tỉnh/thành người nhận và loại phiếu đặt hàng.");
        }
        Map<String, Object> loai = loaiGiao(maLoai);
        BigDecimal nguong = (BigDecimal) loai.get("NguongMienPhi");
        if (nguong != null && tongHang != null && tongHang.compareTo(nguong) >= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal phi = db.scalar("SELECT b.Phi FROM BangPhiGiao b JOIN TinhThanh t ON b.MaKV = t.MaKV WHERE t.MaTinh = :t AND b.MaLoai = :l",
                p("t", maTinh, "l", maLoai), BigDecimal.class);
        if (phi == null) {
            throw new NghiepVuException("Chưa có bảng phí giao hàng cho khu vực / loại phiếu đã chọn.");
        }
        return phi;
    }
}

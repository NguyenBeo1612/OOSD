package com.eshop.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Giỏ hàng hiện tại của khách (lưu trong phiên làm việc – xem GioHangConfig). */
public class GioHang implements Serializable {

    private final Map<String, MucGioHang> muc = new LinkedHashMap<>();

    /** Thêm sản phẩm; nếu đã có thì cộng dồn số lượng. */
    public void them(MucGioHang m) {
        MucGioHang cu = muc.get(m.getMaSP());
        if (cu == null) {
            muc.put(m.getMaSP(), m);
        } else {
            cu.setSoLuong(cu.getSoLuong() + m.getSoLuong());
            cu.setDonGia(m.getDonGia());
        }
    }

    public boolean capNhat(String maSP, int soLuong) {
        MucGioHang m = muc.get(maSP);
        if (m == null) {
            return false;
        }
        if (soLuong <= 0) {
            muc.remove(maSP);
        } else {
            m.setSoLuong(soLuong);
        }
        return true;
    }

    public void xoa(String maSP) {
        muc.remove(maSP);
    }

    public List<MucGioHang> getDanhSach() {
        return new ArrayList<>(muc.values());
    }

    public int getSoLuong() {
        return muc.values().stream().mapToInt(MucGioHang::getSoLuong).sum();
    }

    public BigDecimal getTongTien() {
        return muc.values().stream().map(MucGioHang::getThanhTien).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isRong() {
        return muc.isEmpty();
    }

    public void lamRong() {
        muc.clear();
    }
}

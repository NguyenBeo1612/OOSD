package com.eshop.service;

import java.io.Serializable;
import java.math.BigDecimal;

/** Một dòng trong giỏ hàng. */
public class MucGioHang implements Serializable {

    private final String maSP;
    private final String tenSP;
    private final String hinh;
    private BigDecimal donGia;
    private int soLuong;

    public MucGioHang(String maSP, String tenSP, String hinh, BigDecimal donGia, int soLuong) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.hinh = hinh;
        this.donGia = donGia;
        this.soLuong = soLuong;
    }

    public String getMaSP() {
        return maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public String getHinh() {
        return hinh;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getThanhTien() {
        return donGia.multiply(BigDecimal.valueOf(soLuong));
    }
}

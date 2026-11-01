package com.thuvien.qltv.domain;

import jakarta.persistence.*;

/** Nhà xuất bản – đề chỉ quy định mã, địa chỉ, số điện thoại (BR13). */
@Entity
@Table(name = "NhaXuatBan")
public class NhaXuatBan {

    @Id
    @Column(name = "MaNhaXuatBan", length = 35)
    private String maNhaXuatBan;

    @Column(name = "DiaChi")
    private String diaChi;

    @Column(name = "SoDienThoai")
    private String soDienThoai;

    public NhaXuatBan() {
    }

    public String getMaNhaXuatBan() {
        return maNhaXuatBan;
    }

    public void setMaNhaXuatBan(String maNhaXuatBan) {
        this.maNhaXuatBan = maNhaXuatBan;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
}

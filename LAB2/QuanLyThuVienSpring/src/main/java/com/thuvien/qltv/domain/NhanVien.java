package com.thuvien.qltv.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

/** Nhân viên thư viện (thủ thư, nhân viên quản lý sách). */
@Entity
@Table(name = "NhanVien")
public class NhanVien {

    @Id
    @Column(name = "MaNhanVien", length = 35)
    private String maNhanVien;

    @Column(name = "Ho")
    private String ho;

    @Column(name = "Ten")
    private String ten;

    @Column(name = "Phai")
    private String phai;

    @Column(name = "NgaySinh")
    private LocalDate ngaySinh;

    @Column(name = "ChucVu")
    private String chucVu;

    @Column(name = "SoDienThoai")
    private String soDienThoai;

    public NhanVien() {
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getHo() {
        return ho;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getPhai() {
        return phai;
    }

    public void setPhai(String phai) {
        this.phai = phai;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getHoTen() {
        return (ho == null ? "" : ho) + " " + (ten == null ? "" : ten);
    }
}

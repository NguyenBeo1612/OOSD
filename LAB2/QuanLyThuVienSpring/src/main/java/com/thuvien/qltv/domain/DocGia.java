package com.thuvien.qltv.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

/** Độc giả của thư viện. */
@Entity
@Table(name = "DocGia")
public class DocGia {

    @Id
    @Column(name = "MaDocGia", length = 35)
    private String maDocGia;

    @Column(name = "Ho")
    private String ho;

    @Column(name = "Ten")
    private String ten;

    @Column(name = "NgaySinh")
    private LocalDate ngaySinh;

    @Column(name = "Phai")
    private String phai;

    @Column(name = "SoDienThoai")
    private String soDienThoai;

    @Column(name = "DiaChi")
    private String diaChi;

    @Column(name = "Email")
    private String email;

    @Column(name = "Anh3x4")
    private String anh3x4;

    public DocGia() {
    }

    public String getMaDocGia() {
        return maDocGia;
    }

    public void setMaDocGia(String maDocGia) {
        this.maDocGia = maDocGia;
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

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getPhai() {
        return phai;
    }

    public void setPhai(String phai) {
        this.phai = phai;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAnh3x4() {
        return anh3x4;
    }

    public void setAnh3x4(String anh3x4) {
        this.anh3x4 = anh3x4;
    }

    public String getHoTen() {
        return (ho == null ? "" : ho) + " " + (ten == null ? "" : ten);
    }
}

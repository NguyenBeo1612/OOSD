package com.thuvien.qltv.domain;

import jakarta.persistence.*;

/** Đầu sách: mỗi đầu sách thuộc đúng một thể loại và một nhà xuất bản. */
@Entity
@Table(name = "DauSach")
public class DauSach {

    @Id
    @Column(name = "MaDauSach", length = 35)
    private String maDauSach;

    @Column(name = "TenSach")
    private String tenSach;

    @Column(name = "NamXuatBan")
    private Integer namXuatBan;

    @Column(name = "SoLuongHienCo")
    private Integer soLuongHienCo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaTheLoai")
    private TheLoai theLoai;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaNhaXuatBan")
    private NhaXuatBan nhaXuatBan;

    public DauSach() {
    }

    public String getMaDauSach() {
        return maDauSach;
    }

    public void setMaDauSach(String maDauSach) {
        this.maDauSach = maDauSach;
    }

    public String getTenSach() {
        return tenSach;
    }

    public void setTenSach(String tenSach) {
        this.tenSach = tenSach;
    }

    public Integer getNamXuatBan() {
        return namXuatBan;
    }

    public void setNamXuatBan(Integer namXuatBan) {
        this.namXuatBan = namXuatBan;
    }

    public Integer getSoLuongHienCo() {
        return soLuongHienCo;
    }

    public void setSoLuongHienCo(Integer soLuongHienCo) {
        this.soLuongHienCo = soLuongHienCo;
    }

    public TheLoai getTheLoai() {
        return theLoai;
    }

    public void setTheLoai(TheLoai theLoai) {
        this.theLoai = theLoai;
    }

    public NhaXuatBan getNhaXuatBan() {
        return nhaXuatBan;
    }

    public void setNhaXuatBan(NhaXuatBan nhaXuatBan) {
        this.nhaXuatBan = nhaXuatBan;
    }

    public void giamTon() {
        if (soLuongHienCo == null || soLuongHienCo <= 0) {
            throw new IllegalStateException("Đầu sách " + maDauSach + " đã hết trong kho.");
        }
        soLuongHienCo = soLuongHienCo - 1;
    }

    public void tangTon() {
        soLuongHienCo = (soLuongHienCo == null ? 0 : soLuongHienCo) + 1;
    }
}

package com.thuvien.qltv.domain;

import jakarta.persistence.*;

/** Thể loại sách – một thể loại có nhiều đầu sách (BR12). */
@Entity
@Table(name = "TheLoai")
public class TheLoai {

    @Id
    @Column(name = "MaTheLoai", length = 35)
    private String maTheLoai;

    @Column(name = "TenTheLoai")
    private String tenTheLoai;

    public TheLoai() {
    }

    public String getMaTheLoai() {
        return maTheLoai;
    }

    public void setMaTheLoai(String maTheLoai) {
        this.maTheLoai = maTheLoai;
    }

    public String getTenTheLoai() {
        return tenTheLoai;
    }

    public void setTenTheLoai(String tenTheLoai) {
        this.tenTheLoai = tenTheLoai;
    }
}

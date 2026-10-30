package com.example.QuanLyCaoOc.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Tang")
public class Tang {

    @Id
    @Column(name = "MaTang", length = 10)
    private String maTang;

    @Column(name = "TenTang", nullable = false, length = 50)
    private String tenTang;

    @Column(name = "HeSoTang", precision = 3, scale = 2)
    private BigDecimal heSoTang;

    public Tang() {
    }

    public String getMaTang() {
        return maTang;
    }

    public void setMaTang(String maTang) {
        this.maTang = maTang;
    }

    public String getTenTang() {
        return tenTang;
    }

    public void setTenTang(String tenTang) {
        this.tenTang = tenTang;
    }

    public BigDecimal getHeSoTang() {
        return heSoTang;
    }

    public void setHeSoTang(BigDecimal heSoTang) {
        this.heSoTang = heSoTang;
    }
}
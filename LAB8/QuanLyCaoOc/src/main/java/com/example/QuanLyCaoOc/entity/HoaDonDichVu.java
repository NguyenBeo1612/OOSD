package com.example.QuanLyCaoOc.entity;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class HoaDonDichVu {
    private String soHDDV;
    private String soHopDong;
    private Integer thang;
    private Integer nam;
    private BigDecimal tienDien;
    private BigDecimal tienNuoc;
    private BigDecimal tienInternet;
    private BigDecimal tongTienDV;
    private String trangThaiThanhToan;
}
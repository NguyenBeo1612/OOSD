package com.example.QuanLyCaoOc.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class HoaDonThuePhong {
    private String soHDThue;
    private String soHopDong;
    private Integer thang;
    private Integer nam;
    private BigDecimal soTienThue;
    private LocalDate hanThanhToan;
    private String trangThaiThanhToan;
}
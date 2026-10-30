package com.example.QuanLyCaoOc.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class HopDongThue {
    private String soHopDong;
    private String maKH;
    private String tenCongTy; // Hiển thị tên công ty khi JOIN
    private String maPhong;   // Hiển thị mã phòng từ ChiTietHopDong
    private LocalDate ngayLap;
    private LocalDate ngayBatDau;
    private Integer thoiHanThue;
    private BigDecimal donGiaThue;
    private BigDecimal tongTienThue6Thang;
    private String trangThai;
}
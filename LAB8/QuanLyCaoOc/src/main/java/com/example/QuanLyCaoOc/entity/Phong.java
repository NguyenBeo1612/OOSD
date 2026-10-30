package com.example.QuanLyCaoOc.entity;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Phong {
    private String maPhong;
    private String tenTang;
    private BigDecimal dienTich;
    private String huongPhong;
    private BigDecimal donGiaNiemYet;
    private String trangThai;
}
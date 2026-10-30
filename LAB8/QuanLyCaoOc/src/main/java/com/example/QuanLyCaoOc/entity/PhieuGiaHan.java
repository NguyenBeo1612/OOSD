package com.example.QuanLyCaoOc.entity;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PhieuGiaHan {
    private String soPhieuGH;
    private String soHopDong;
    private String maPhong;
    private LocalDate ngayLapGH;
    private Integer thoiGianGiaHan;
    private String trangThai;
}
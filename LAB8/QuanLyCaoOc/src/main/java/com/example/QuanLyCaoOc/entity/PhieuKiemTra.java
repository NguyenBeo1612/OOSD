package com.example.QuanLyCaoOc.entity;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PhieuKiemTra {
    private String soPhieuKT;
    private String maPhong;
    private LocalDate ngayKiemTra;
    private String tinhTrangPhong;
    private String nguoiKiemTra;
}
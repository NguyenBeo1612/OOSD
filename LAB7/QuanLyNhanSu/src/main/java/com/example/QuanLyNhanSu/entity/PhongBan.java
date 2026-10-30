package com.example.QuanLyNhanSu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "phong_ban")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PhongBan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maPhongBan;

    @Column(nullable = false, unique = true, length = 100)
    private String tenPhongBan;

    private String moTa;

    @OneToMany(mappedBy = "phongBan", cascade = CascadeType.ALL)
    private List<NhanVien> dsNhanVien;
}
package com.example.QuanLyNhanSu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "quyet_dinh_tuyen_dung")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
public class QuyetDinhTuyenDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long soQDTD;

    private LocalDate ngayKy = LocalDate.now();
    private LocalDate ngayBatDauThuViec;
    private LocalDate ngayKetThucThuViec;
    private BigDecimal luongThuViec;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String noiDungDieuKhoan;

    // Khai báo mối quan hệ với NhanVien
    @OneToOne
    @JoinColumn(name = "ma_nv", nullable = false, unique = true)
    private NhanVien nhanVien;

    // Viết trực tiếp Getter/Setter để đảm bảo không bị lỗi biên dịch (dù có Lombok hay không)
    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public Long getSoQDTD() {
        return soQDTD;
    }

    public void setSoQDTD(Long soQDTD) {
        this.soQDTD = soQDTD;
    }

    public LocalDate getNgayKy() {
        return ngayKy;
    }

    public void setNgayKy(LocalDate ngayKy) {
        this.ngayKy = ngayKy;
    }

    public LocalDate getNgayBatDauThuViec() {
        return ngayBatDauThuViec;
    }

    public void setNgayBatDauThuViec(LocalDate ngayBatDauThuViec) {
        this.ngayBatDauThuViec = ngayBatDauThuViec;
    }

    public LocalDate getNgayKetThucThuViec() {
        return ngayKetThucThuViec;
    }

    public void setNgayKetThucThuViec(LocalDate ngayKetThucThuViec) {
        this.ngayKetThucThuViec = ngayKetThucThuViec;
    }

    public BigDecimal getLuongThuViec() {
        return luongThuViec;
    }

    public void setLuongThuViec(BigDecimal luongThuViec) {
        this.luongThuViec = luongThuViec;
    }

    public String getNoiDungDieuKhoan() {
        return noiDungDieuKhoan;
    }

    public void setNoiDungDieuKhoan(String noiDungDieuKhoan) {
        this.noiDungDieuKhoan = noiDungDieuKhoan;
    }
}
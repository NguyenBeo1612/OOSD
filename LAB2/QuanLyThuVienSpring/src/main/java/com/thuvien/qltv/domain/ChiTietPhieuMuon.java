package com.thuvien.qltv.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

/** Mỗi dòng là một đầu sách trong phiếu; UNIQUE(MaPhieuMuon, MaDauSach) (BR06). */
@Entity
@Table(name = "ChiTietPhieuMuon")
public class ChiTietPhieuMuon {

    @Id
    @Column(name = "MaChiTiet", length = 35)
    private String maChiTiet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaPhieuMuon")
    private PhieuMuon phieuMuon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaDauSach")
    private DauSach dauSach;

    @Column(name = "NgayTraThucTe")
    private LocalDate ngayTraThucTe;

    @Column(name = "TinhTrangTra")
    private String tinhTrangTra;

    public ChiTietPhieuMuon() {
    }

    public String getMaChiTiet() {
        return maChiTiet;
    }

    public void setMaChiTiet(String maChiTiet) {
        this.maChiTiet = maChiTiet;
    }

    public PhieuMuon getPhieuMuon() {
        return phieuMuon;
    }

    public void setPhieuMuon(PhieuMuon phieuMuon) {
        this.phieuMuon = phieuMuon;
    }

    public DauSach getDauSach() {
        return dauSach;
    }

    public void setDauSach(DauSach dauSach) {
        this.dauSach = dauSach;
    }

    public LocalDate getNgayTraThucTe() {
        return ngayTraThucTe;
    }

    public void setNgayTraThucTe(LocalDate ngayTraThucTe) {
        this.ngayTraThucTe = ngayTraThucTe;
    }

    public String getTinhTrangTra() {
        return tinhTrangTra;
    }

    public void setTinhTrangTra(String tinhTrangTra) {
        this.tinhTrangTra = tinhTrangTra;
    }

    public boolean daTra() {
        return ngayTraThucTe != null;
    }

    public boolean quaHan(LocalDate ngay) {
        return ngayTraThucTe == null && phieuMuon != null && phieuMuon.getNgayHenTra().isBefore(ngay);
    }
}

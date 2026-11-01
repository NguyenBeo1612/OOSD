package com.thuvien.qltv.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Phiếu mượn: một độc giả, một nhân viên lập, 1..3 chi tiết. */
@Entity
@Table(name = "PhieuMuon")
public class PhieuMuon {

    @Id
    @Column(name = "MaPhieuMuon", length = 35)
    private String maPhieuMuon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaDocGia")
    private DocGia docGia;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaNhanVien")
    private NhanVien nhanVien;

    @Column(name = "NgayMuon")
    private LocalDate ngayMuon;

    @Column(name = "NgayHenTra")
    private LocalDate ngayHenTra;

    @OneToMany(mappedBy = "phieuMuon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietPhieuMuon> chiTiet = new ArrayList<>();

    public PhieuMuon() {
    }

    public String getMaPhieuMuon() {
        return maPhieuMuon;
    }

    public void setMaPhieuMuon(String maPhieuMuon) {
        this.maPhieuMuon = maPhieuMuon;
    }

    public DocGia getDocGia() {
        return docGia;
    }

    public void setDocGia(DocGia docGia) {
        this.docGia = docGia;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public LocalDate getNgayMuon() {
        return ngayMuon;
    }

    public void setNgayMuon(LocalDate ngayMuon) {
        this.ngayMuon = ngayMuon;
    }

    public LocalDate getNgayHenTra() {
        return ngayHenTra;
    }

    public void setNgayHenTra(LocalDate ngayHenTra) {
        this.ngayHenTra = ngayHenTra;
    }

    public List<ChiTietPhieuMuon> getChiTiet() {
        return chiTiet;
    }

    public void setChiTiet(List<ChiTietPhieuMuon> chiTiet) {
        this.chiTiet = chiTiet;
    }

    public void themChiTiet(ChiTietPhieuMuon ct) {
        ct.setPhieuMuon(this);
        chiTiet.add(ct);
    }
}

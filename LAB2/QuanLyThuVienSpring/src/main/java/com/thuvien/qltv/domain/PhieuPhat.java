package com.thuvien.qltv.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Phiếu phạt: ngày phạt, lý do, phí phạt từng cuốn và nhân viên lập (BR09, BR10). */
@Entity
@Table(name = "PhieuPhat")
public class PhieuPhat {

    @Id
    @Column(name = "MaPhieuPhat", length = 35)
    private String maPhieuPhat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaChiTiet")
    private ChiTietPhieuMuon chiTiet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaNhanVien")
    private NhanVien nhanVien;

    @Column(name = "NgayPhat")
    private LocalDate ngayPhat;

    @Column(name = "LyDo")
    private String lyDo;

    @Column(name = "PhiPhat")
    private BigDecimal phiPhat;

    public PhieuPhat() {
    }

    public String getMaPhieuPhat() {
        return maPhieuPhat;
    }

    public void setMaPhieuPhat(String maPhieuPhat) {
        this.maPhieuPhat = maPhieuPhat;
    }

    public ChiTietPhieuMuon getChiTiet() {
        return chiTiet;
    }

    public void setChiTiet(ChiTietPhieuMuon chiTiet) {
        this.chiTiet = chiTiet;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public LocalDate getNgayPhat() {
        return ngayPhat;
    }

    public void setNgayPhat(LocalDate ngayPhat) {
        this.ngayPhat = ngayPhat;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }

    public BigDecimal getPhiPhat() {
        return phiPhat;
    }

    public void setPhiPhat(BigDecimal phiPhat) {
        this.phiPhat = phiPhat;
    }
}

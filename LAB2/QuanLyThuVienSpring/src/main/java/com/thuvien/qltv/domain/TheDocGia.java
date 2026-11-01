package com.thuvien.qltv.domain;

import jakarta.persistence.*;
import java.time.LocalDate;

/** Thẻ thư viện; lưu lịch sử cấp/gia hạn, chỉ một thẻ TrangThai = 1 cho mỗi độc giả (BR02). */
@Entity
@Table(name = "TheDocGia")
public class TheDocGia {

    @Id
    @Column(name = "MaThe", length = 35)
    private String maThe;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MaDocGia")
    private DocGia docGia;

    @Column(name = "NgayCap")
    private LocalDate ngayCap;

    @Column(name = "HanSuDung")
    private LocalDate hanSuDung;

    @Column(name = "DaDongLePhi")
    private Boolean daDongLePhi;

    @Column(name = "TrangThai")
    private Boolean trangThai;

    public TheDocGia() {
    }

    public String getMaThe() {
        return maThe;
    }

    public void setMaThe(String maThe) {
        this.maThe = maThe;
    }

    public DocGia getDocGia() {
        return docGia;
    }

    public void setDocGia(DocGia docGia) {
        this.docGia = docGia;
    }

    public LocalDate getNgayCap() {
        return ngayCap;
    }

    public void setNgayCap(LocalDate ngayCap) {
        this.ngayCap = ngayCap;
    }

    public LocalDate getHanSuDung() {
        return hanSuDung;
    }

    public void setHanSuDung(LocalDate hanSuDung) {
        this.hanSuDung = hanSuDung;
    }

    public Boolean getDaDongLePhi() {
        return daDongLePhi;
    }

    public void setDaDongLePhi(Boolean daDongLePhi) {
        this.daDongLePhi = daDongLePhi;
    }

    public Boolean getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Boolean trangThai) {
        this.trangThai = trangThai;
    }

    /** BR03: thẻ còn hạn, đã đóng lệ phí và đang hoạt động mới có giá trị. */
    public boolean conGiaTri(LocalDate ngay) {
        return Boolean.TRUE.equals(trangThai) && Boolean.TRUE.equals(daDongLePhi)
                && hanSuDung != null && !hanSuDung.isBefore(ngay);
    }
}

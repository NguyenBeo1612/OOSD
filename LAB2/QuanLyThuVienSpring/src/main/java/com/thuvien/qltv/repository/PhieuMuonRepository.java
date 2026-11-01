package com.thuvien.qltv.repository;

import com.thuvien.qltv.domain.PhieuMuon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhieuMuonRepository extends JpaRepository<PhieuMuon, String> {

    boolean existsByDocGia_MaDocGia(String maDocGia);

    boolean existsByNhanVien_MaNhanVien(String maNhanVien);

    long countByDocGia_MaDocGia(String maDocGia);
}

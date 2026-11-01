package com.thuvien.qltv.repository;

import com.thuvien.qltv.domain.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, String> {

    boolean existsByNhanVien_MaNhanVien(String maNhanVien);
}

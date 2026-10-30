package com.example.QuanLyNhanSu.repository;

import com.example.QuanLyNhanSu.entity.QuyetDinhTuyenDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuyetDinhTuyenDungRepository extends JpaRepository<QuyetDinhTuyenDung, Long> {
}

package com.example.QuanLyNhanSu.repository;

import com.example.QuanLyNhanSu.entity.QuyetDinhKhenThuongKyLuat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuyetDinhKTKLRepository extends JpaRepository<QuyetDinhKhenThuongKyLuat, Long> {
}

package com.thuvien.qltv.repository;

import com.thuvien.qltv.domain.NhanVien;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NhanVienRepository extends JpaRepository<NhanVien, String> {

    List<NhanVien> findAllByOrderByMaNhanVienAsc();
}

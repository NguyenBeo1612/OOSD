package com.thuvien.qltv.repository;

import com.thuvien.qltv.domain.TheLoai;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheLoaiRepository extends JpaRepository<TheLoai, String> {

    List<TheLoai> findAllByOrderByMaTheLoaiAsc();

    boolean existsByTenTheLoaiIgnoreCaseAndMaTheLoaiNot(String tenTheLoai, String maTheLoai);
}

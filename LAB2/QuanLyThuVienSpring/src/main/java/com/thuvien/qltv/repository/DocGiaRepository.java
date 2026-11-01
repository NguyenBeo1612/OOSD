package com.thuvien.qltv.repository;

import com.thuvien.qltv.domain.DocGia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocGiaRepository extends JpaRepository<DocGia, String> {

    List<DocGia> findAllByOrderByMaDocGiaAsc();
}

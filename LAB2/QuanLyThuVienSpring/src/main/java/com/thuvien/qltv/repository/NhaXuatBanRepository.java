package com.thuvien.qltv.repository;

import com.thuvien.qltv.domain.NhaXuatBan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NhaXuatBanRepository extends JpaRepository<NhaXuatBan, String> {

    List<NhaXuatBan> findAllByOrderByMaNhaXuatBanAsc();
}

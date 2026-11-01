package com.thuvien.qltv.repository;

import com.thuvien.qltv.domain.TheDocGia;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TheDocGiaRepository extends JpaRepository<TheDocGia, String> {

    @Query("select t from TheDocGia t where t.docGia.maDocGia = :ma and t.trangThai = true order by t.hanSuDung desc")
    List<TheDocGia> findTheHoatDong(@Param("ma") String maDocGia);

    @Query("select count(t) from TheDocGia t where t.docGia.maDocGia = :ma and t.trangThai = true and t.hanSuDung >= :ngay")
    long demTheConGiaTri(@Param("ma") String maDocGia, @Param("ngay") LocalDate ngay);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update TheDocGia t set t.trangThai = false where t.docGia.maDocGia = :ma and t.trangThai = true")
    int voHieuTheCu(@Param("ma") String maDocGia);

    boolean existsByDocGia_MaDocGia(String maDocGia);
}

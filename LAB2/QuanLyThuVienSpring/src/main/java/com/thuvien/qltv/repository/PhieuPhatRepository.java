package com.thuvien.qltv.repository;

import com.thuvien.qltv.domain.PhieuPhat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PhieuPhatRepository extends JpaRepository<PhieuPhat, String> {

    @Query("select sum(p.phiPhat) from PhieuPhat p where p.ngayPhat between :tu and :den")
    /** Trả về null khi không có phiếu phạt. */
    BigDecimal tongPhiPhat(@Param("tu") LocalDate tu, @Param("den") LocalDate den);

    @Query("select p from PhieuPhat p join fetch p.chiTiet c join fetch c.phieuMuon pm join fetch pm.docGia "
            + "join fetch c.dauSach join fetch p.nhanVien where p.ngayPhat between :tu and :den "
            + "order by p.ngayPhat desc, p.maPhieuPhat")
    List<PhieuPhat> chiTietPhat(@Param("tu") LocalDate tu, @Param("den") LocalDate den);

    List<PhieuPhat> findByChiTiet_MaChiTiet(String maChiTiet);

    boolean existsByNhanVien_MaNhanVien(String maNhanVien);
}

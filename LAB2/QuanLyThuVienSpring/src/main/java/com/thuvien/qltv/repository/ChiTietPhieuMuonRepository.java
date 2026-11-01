package com.thuvien.qltv.repository;

import com.thuvien.qltv.domain.ChiTietPhieuMuon;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChiTietPhieuMuonRepository extends JpaRepository<ChiTietPhieuMuon, String> {

    @Query("select count(c) from ChiTietPhieuMuon c where c.phieuMuon.docGia.maDocGia = :ma and c.ngayTraThucTe is null")
    long demDangMuon(@Param("ma") String maDocGia);

    @Query("select count(c) from ChiTietPhieuMuon c where c.phieuMuon.docGia.maDocGia = :ma "
            + "and c.ngayTraThucTe is null and c.phieuMuon.ngayHenTra < :homNay")
    long demQuaHan(@Param("ma") String maDocGia, @Param("homNay") LocalDate homNay);

    @Query("select c from ChiTietPhieuMuon c join fetch c.phieuMuon p join fetch c.dauSach "
            + "where p.docGia.maDocGia = :ma and c.ngayTraThucTe is null order by p.ngayHenTra, c.maChiTiet")
    List<ChiTietPhieuMuon> findDangMuon(@Param("ma") String maDocGia);

    boolean existsByDauSach_MaDauSach(String maDauSach);

    // ---------- thống kê ----------
    @Query("select count(c) from ChiTietPhieuMuon c where c.phieuMuon.ngayMuon between :tu and :den")
    long demLuotMuon(@Param("tu") LocalDate tu, @Param("den") LocalDate den);

    @Query("select count(c) from ChiTietPhieuMuon c join c.phieuMuon p where "
            + "(c.ngayTraThucTe is null and p.ngayHenTra < :homNay) "
            + "or (c.ngayTraThucTe is not null and c.ngayTraThucTe > p.ngayHenTra and c.ngayTraThucTe between :tu and :den)")
    long demQuaHanTrongKy(@Param("tu") LocalDate tu, @Param("den") LocalDate den, @Param("homNay") LocalDate homNay);

    @Query("select count(c) from ChiTietPhieuMuon c where c.ngayTraThucTe between :tu and :den and c.tinhTrangTra = :tt")
    long demTheoTinhTrang(@Param("tu") LocalDate tu, @Param("den") LocalDate den, @Param("tt") String tinhTrang);
}

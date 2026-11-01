package com.thuvien.qltv.repository;

import com.thuvien.qltv.domain.DauSach;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DauSachRepository extends JpaRepository<DauSach, String> {

    /** Khóa dòng khi lập phiếu (SQL Server: WITH (UPDLOCK, HOLDLOCK, ROWLOCK)). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from DauSach s where s.maDauSach = :ma")
    Optional<DauSach> findByIdForUpdate(@Param("ma") String ma);

    @Query("select s from DauSach s where lower(s.maDauSach) like lower(concat('%', :k, '%')) "
            + "or lower(s.tenSach) like lower(concat('%', :k, '%')) order by s.maDauSach")
    List<DauSach> timKiem(@Param("k") String tuKhoa);

    List<DauSach> findAllByOrderByMaDauSachAsc();

    List<DauSach> findBySoLuongHienCoGreaterThanOrderByTenSachAsc(Integer soLuong);

    boolean existsByTheLoai_MaTheLoai(String maTheLoai);

    boolean existsByNhaXuatBan_MaNhaXuatBan(String maNhaXuatBan);
}

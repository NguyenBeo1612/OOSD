package com.example.QuanLyNhanSu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "quyet_dinh_ktkl")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
public class QuyetDinhKhenThuongKyLuat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long soQDKTKL;

    private String loaiQuyetDinh; // Khen thưởng / Kỷ luật
    private String doiTuong;      // Cá nhân / Tập thể
    private LocalDate ngayKy = LocalDate.now();
    private String lyDo;
    private BigDecimal soTien;

    @ManyToOne
    @JoinColumn(name = "ma_nv", nullable = true)
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "ma_phong_ban", nullable = true)
    private PhongBan phongBan;

    // Trong trường hợp IDE không nhận Lombok, bạn thêm trực tiếp Getter/Setter này:
    public String getLoaiQuyetDinh() {
        return loaiQuyetDinh;
    }

    public void setLoaiQuyetDinh(String loaiQuyetDinh) {
        this.loaiQuyetDinh = loaiQuyetDinh;
    }
}
package com.khachsan.qlks.service;

import static com.khachsan.qlks.service.Db.p;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** UC09 – Quản lý danh mục: khu vực, nhân viên, loại tiện nghi, dịch vụ, quy định đền bù (DanhMucService.cs). */
@Service
@Transactional
public class DanhMucService {

    /** loại danh mục -> {bảng, cột khóa} – chỉ cho phép xóa trong danh sách này. */
    private static final Map<String, String[]> BANG = Map.of(
            "khu-vuc", new String[]{"KhuVuc", "MaKhuVuc"},
            "nhan-vien", new String[]{"NhanVien", "MaNV"},
            "loai-tn", new String[]{"LoaiTienNghi", "MaLoaiTN"},
            "dich-vu", new String[]{"DichVu", "MaDV"},
            "quy-dinh", new String[]{"QuyDinhDenBu", "MaQuyDinh"});

    private final Db db;

    public DanhMucService(Db db) {
        this.db = db;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> layKhuVuc() {
        return db.query("SELECT * FROM KhuVuc ORDER BY MaKhuVuc");
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> layNhanVien() {
        return db.query("SELECT * FROM NhanVien ORDER BY MaNV");
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> layLoaiTienNghi() {
        return db.query("SELECT * FROM LoaiTienNghi ORDER BY MaLoaiTN");
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> layDichVu() {
        return db.query("SELECT * FROM DichVu ORDER BY MaDV");
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> layQuyDinhDenBu() {
        return db.query("SELECT q.*, l.TenLoaiTN FROM QuyDinhDenBu q JOIN LoaiTienNghi l ON q.MaLoaiTN = l.MaLoaiTN ORDER BY q.MaQuyDinh");
    }

    private String them(String sql, Map<String, Object> params, String ok) {
        try {
            db.execute(sql, params);
            return ok;
        } catch (DataIntegrityViolationException ex) {
            throw new NghiepVuException("Mã hoặc tên đã tồn tại / dữ liệu tham chiếu không hợp lệ.");
        }
    }

    public String themKhuVuc(String ma, String ten) {
        KiemTra.batBuoc("Mã khu vực và tên khu vực không được để trống.", ma, ten);
        return them("INSERT INTO KhuVuc(MaKhuVuc, TenKhuVuc) VALUES(:m, :t)", p("m", ma.trim(), "t", ten.trim()), "Đã thêm khu vực.");
    }

    public String themNhanVien(String ma, String ten, String vaiTro, String sdt) {
        KiemTra.batBuoc("Thông tin nhân viên chưa đầy đủ.", ma, ten, vaiTro);
        if (!TrangThai.VAI_TRO_NV.contains(vaiTro)) {
            throw new NghiepVuException("Vai trò không hợp lệ.");
        }
        return them("INSERT INTO NhanVien(MaNV, HoTen, VaiTro, SoDienThoai) VALUES(:m, :t, :v, :s)",
                p("m", ma.trim(), "t", ten.trim(), "v", vaiTro, "s", KiemTra.rong(sdt) ? null : sdt.trim()), "Đã thêm nhân viên.");
    }

    public String themLoaiTienNghi(String ma, String ten) {
        KiemTra.batBuoc("Thông tin loại tiện nghi chưa đủ.", ma, ten);
        return them("INSERT INTO LoaiTienNghi(MaLoaiTN, TenLoaiTN) VALUES(:m, :t)", p("m", ma.trim(), "t", ten.trim()), "Đã thêm loại tiện nghi.");
    }

    public String themDichVu(String ma, String ten, String dvt, BigDecimal gia) {
        KiemTra.batBuoc("Thông tin dịch vụ không hợp lệ.", ma, ten, dvt);
        if (gia == null || gia.signum() < 0) {
            throw new NghiepVuException("Đơn giá dịch vụ phải >= 0.");
        }
        return them("INSERT INTO DichVu(MaDV, TenDV, DonViTinh, DonGia) VALUES(:m, :t, :d, :g)",
                p("m", ma.trim(), "t", ten.trim(), "d", dvt.trim(), "g", gia), "Đã thêm dịch vụ.");
    }

    public String themQuyDinh(String ma, String loai, String mucDo, BigDecimal tien) {
        KiemTra.batBuoc("Quy định đền bù không hợp lệ.", ma, loai, mucDo);
        if (tien == null || tien.signum() < 0) {
            throw new NghiepVuException("Mức đền bù phải >= 0.");
        }
        return them("INSERT INTO QuyDinhDenBu(MaQuyDinh, MaLoaiTN, MucDoThietHai, MucDenBu) VALUES(:m, :l, :u, :t)",
                p("m", ma.trim(), "l", loai, "u", mucDo.trim(), "t", tien), "Đã thêm quy định đền bù.");
    }

    /** Xóa – FK chặn xóa dữ liệu đã phát sinh nghiệp vụ, thông báo dễ hiểu thay vì lỗi SQL. */
    public String xoa(String loai, String ma) {
        String[] b = BANG.get(loai);
        if (b == null) {
            throw new NghiepVuException("Loại danh mục không hợp lệ.");
        }
        try {
            int n = db.execute("DELETE FROM " + b[0] + " WHERE " + b[1] + " = :m", p("m", ma));
            if (n == 0) {
                throw new NghiepVuException("Không tìm thấy " + ma + ".");
            }
            return "Đã xóa " + ma + ".";
        } catch (DataIntegrityViolationException ex) {
            throw new NghiepVuException(ma + " đang được sử dụng ở dữ liệu nghiệp vụ, không thể xóa.");
        }
    }
}

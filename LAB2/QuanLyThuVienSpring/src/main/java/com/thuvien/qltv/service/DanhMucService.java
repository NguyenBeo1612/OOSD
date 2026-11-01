package com.thuvien.qltv.service;

import com.thuvien.qltv.domain.NhaXuatBan;
import com.thuvien.qltv.domain.NhanVien;
import com.thuvien.qltv.domain.TheLoai;
import com.thuvien.qltv.repository.DauSachRepository;
import com.thuvien.qltv.repository.NhaXuatBanRepository;
import com.thuvien.qltv.repository.NhanVienRepository;
import com.thuvien.qltv.repository.PhieuMuonRepository;
import com.thuvien.qltv.repository.PhieuPhatRepository;
import com.thuvien.qltv.repository.TaiKhoanRepository;
import com.thuvien.qltv.repository.TheLoaiRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** CRUD Nhân viên, Thể loại, Nhà xuất bản (tương ứng DanhMucService.cs). */
@Service
@Transactional
public class DanhMucService {

    private final NhanVienRepository nhanVienRepo;
    private final TheLoaiRepository theLoaiRepo;
    private final NhaXuatBanRepository nxbRepo;
    private final DauSachRepository dauSachRepo;
    private final PhieuMuonRepository phieuMuonRepo;
    private final PhieuPhatRepository phieuPhatRepo;
    private final TaiKhoanRepository taiKhoanRepo;

    public DanhMucService(NhanVienRepository nhanVienRepo, TheLoaiRepository theLoaiRepo, NhaXuatBanRepository nxbRepo,
                          DauSachRepository dauSachRepo, PhieuMuonRepository phieuMuonRepo,
                          PhieuPhatRepository phieuPhatRepo, TaiKhoanRepository taiKhoanRepo) {
        this.nhanVienRepo = nhanVienRepo;
        this.theLoaiRepo = theLoaiRepo;
        this.nxbRepo = nxbRepo;
        this.dauSachRepo = dauSachRepo;
        this.phieuMuonRepo = phieuMuonRepo;
        this.phieuPhatRepo = phieuPhatRepo;
        this.taiKhoanRepo = taiKhoanRepo;
    }

    // ===================== NHÂN VIÊN =====================
    @Transactional(readOnly = true)
    public List<NhanVien> layNhanVien() {
        return nhanVienRepo.findAllByOrderByMaNhanVienAsc();
    }

    @Transactional(readOnly = true)
    public NhanVien timNhanVien(String ma) {
        return nhanVienRepo.findById(ma).orElseThrow(() -> new NghiepVuException("Không tìm thấy nhân viên " + ma + "."));
    }

    public String luuNhanVien(NhanVien nv, boolean capNhat) {
        if (nv == null) {
            throw new NghiepVuException("Dữ liệu nhân viên không hợp lệ.");
        }
        KiemTra.batBuoc("Vui lòng nhập đủ Mã, Họ, Tên, Phái và Chức vụ của nhân viên.",
                nv.getMaNhanVien(), nv.getHo(), nv.getTen(), nv.getPhai(), nv.getChucVu());
        if (nv.getNgaySinh() == null) {
            throw new NghiepVuException("Vui lòng nhập ngày sinh nhân viên.");
        }
        nv.setMaNhanVien(KiemTra.sach(nv.getMaNhanVien()));
        nv.setHo(KiemTra.sach(nv.getHo()));
        nv.setTen(KiemTra.sach(nv.getTen()));
        boolean tonTai = nhanVienRepo.existsById(nv.getMaNhanVien());
        if (!capNhat && tonTai) {
            throw new NghiepVuException("Mã nhân viên " + nv.getMaNhanVien() + " đã tồn tại.");
        }
        if (capNhat && !tonTai) {
            throw new NghiepVuException("Không tìm thấy nhân viên cần cập nhật.");
        }
        nhanVienRepo.save(nv);
        return capNhat ? "Cập nhật nhân viên thành công." : "Thêm nhân viên thành công.";
    }

    public String xoaNhanVien(String ma) {
        if (phieuMuonRepo.existsByNhanVien_MaNhanVien(ma) || phieuPhatRepo.existsByNhanVien_MaNhanVien(ma)
                || taiKhoanRepo.existsByNhanVien_MaNhanVien(ma)) {
            throw new NghiepVuException("Nhân viên đang được sử dụng (phiếu mượn / phiếu phạt / tài khoản), không thể xóa.");
        }
        nhanVienRepo.deleteById(ma);
        return "Đã xóa nhân viên " + ma + ".";
    }

    // ===================== THỂ LOẠI =====================
    @Transactional(readOnly = true)
    public List<TheLoai> layTheLoai() {
        return theLoaiRepo.findAllByOrderByMaTheLoaiAsc();
    }

    @Transactional(readOnly = true)
    public TheLoai timTheLoai(String ma) {
        return theLoaiRepo.findById(ma).orElseThrow(() -> new NghiepVuException("Không tìm thấy thể loại " + ma + "."));
    }

    public String luuTheLoai(TheLoai tl, boolean capNhat) {
        KiemTra.batBuoc("Vui lòng nhập Mã và Tên thể loại.", tl.getMaTheLoai(), tl.getTenTheLoai());
        tl.setMaTheLoai(KiemTra.sach(tl.getMaTheLoai()));
        tl.setTenTheLoai(KiemTra.sach(tl.getTenTheLoai()));
        boolean tonTai = theLoaiRepo.existsById(tl.getMaTheLoai());
        if (!capNhat && tonTai) {
            throw new NghiepVuException("Mã thể loại " + tl.getMaTheLoai() + " đã tồn tại.");
        }
        if (theLoaiRepo.existsByTenTheLoaiIgnoreCaseAndMaTheLoaiNot(tl.getTenTheLoai(), tl.getMaTheLoai())) {
            throw new NghiepVuException("Tên thể loại \"" + tl.getTenTheLoai() + "\" đã tồn tại.");
        }
        theLoaiRepo.save(tl);
        return capNhat ? "Cập nhật thể loại thành công." : "Thêm thể loại thành công.";
    }

    public String xoaTheLoai(String ma) {
        if (dauSachRepo.existsByTheLoai_MaTheLoai(ma)) {
            throw new NghiepVuException("Thể loại đang được sử dụng bởi đầu sách, không thể xóa.");
        }
        theLoaiRepo.deleteById(ma);
        return "Đã xóa thể loại " + ma + ".";
    }

    // ===================== NHÀ XUẤT BẢN =====================
    @Transactional(readOnly = true)
    public List<NhaXuatBan> layNhaXuatBan() {
        return nxbRepo.findAllByOrderByMaNhaXuatBanAsc();
    }

    @Transactional(readOnly = true)
    public NhaXuatBan timNhaXuatBan(String ma) {
        return nxbRepo.findById(ma).orElseThrow(() -> new NghiepVuException("Không tìm thấy nhà xuất bản " + ma + "."));
    }

    public String luuNhaXuatBan(NhaXuatBan nxb, boolean capNhat) {
        KiemTra.batBuoc("Vui lòng nhập Mã nhà xuất bản.", nxb.getMaNhaXuatBan());
        nxb.setMaNhaXuatBan(KiemTra.sach(nxb.getMaNhaXuatBan()));
        boolean tonTai = nxbRepo.existsById(nxb.getMaNhaXuatBan());
        if (!capNhat && tonTai) {
            throw new NghiepVuException("Mã nhà xuất bản " + nxb.getMaNhaXuatBan() + " đã tồn tại.");
        }
        nxbRepo.save(nxb);
        return capNhat ? "Cập nhật nhà xuất bản thành công." : "Thêm nhà xuất bản thành công.";
    }

    public String xoaNhaXuatBan(String ma) {
        if (dauSachRepo.existsByNhaXuatBan_MaNhaXuatBan(ma)) {
            throw new NghiepVuException("Nhà xuất bản đang được sử dụng bởi đầu sách, không thể xóa.");
        }
        nxbRepo.deleteById(ma);
        return "Đã xóa nhà xuất bản " + ma + ".";
    }
}

package com.thuvien.qltv.service;

import com.thuvien.qltv.domain.DauSach;
import com.thuvien.qltv.repository.ChiTietPhieuMuonRepository;
import com.thuvien.qltv.repository.DauSachRepository;
import com.thuvien.qltv.repository.NhaXuatBanRepository;
import com.thuvien.qltv.repository.TheLoaiRepository;
import java.time.Year;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Quản lý đầu sách (tương ứng SachService.cs). */
@Service
@Transactional
public class SachService {

    private final DauSachRepository dauSachRepo;
    private final TheLoaiRepository theLoaiRepo;
    private final NhaXuatBanRepository nxbRepo;
    private final ChiTietPhieuMuonRepository chiTietRepo;

    public SachService(DauSachRepository dauSachRepo, TheLoaiRepository theLoaiRepo, NhaXuatBanRepository nxbRepo,
                       ChiTietPhieuMuonRepository chiTietRepo) {
        this.dauSachRepo = dauSachRepo;
        this.theLoaiRepo = theLoaiRepo;
        this.nxbRepo = nxbRepo;
        this.chiTietRepo = chiTietRepo;
    }

    @Transactional(readOnly = true)
    public List<DauSach> layDanhSach(String tuKhoa) {
        return KiemTra.rong(tuKhoa) ? dauSachRepo.findAllByOrderByMaDauSachAsc() : dauSachRepo.timKiem(tuKhoa.trim());
    }

    @Transactional(readOnly = true)
    public List<DauSach> laySachConTrongKho() {
        return dauSachRepo.findBySoLuongHienCoGreaterThanOrderByTenSachAsc(0);
    }

    @Transactional(readOnly = true)
    public DauSach tim(String ma) {
        return dauSachRepo.findById(ma).orElseThrow(() -> new NghiepVuException("Không tìm thấy đầu sách " + ma + "."));
    }

    public String luu(DauSach s, String maTheLoai, String maNhaXuatBan, boolean capNhat) {
        KiemTra.batBuoc("Vui lòng nhập Mã và Tên sách.", s.getMaDauSach(), s.getTenSach());
        KiemTra.batBuoc("Vui lòng chọn Thể loại và Nhà xuất bản (BR12, BR13).", maTheLoai, maNhaXuatBan);
        if (s.getSoLuongHienCo() == null || s.getSoLuongHienCo() < 0) {
            throw new NghiepVuException("Số lượng hiện có phải >= 0.");
        }
        int namNay = Year.now().getValue();
        if (s.getNamXuatBan() == null || s.getNamXuatBan() < 1900 || s.getNamXuatBan() > namNay) {
            throw new NghiepVuException("Năm xuất bản phải trong khoảng 1900 – " + namNay + ".");
        }
        s.setMaDauSach(KiemTra.sach(s.getMaDauSach()));
        s.setTenSach(KiemTra.sach(s.getTenSach()));
        boolean tonTai = dauSachRepo.existsById(s.getMaDauSach());
        if (!capNhat && tonTai) {
            throw new NghiepVuException("Mã đầu sách " + s.getMaDauSach() + " đã tồn tại.");
        }
        if (capNhat && !tonTai) {
            throw new NghiepVuException("Không tìm thấy đầu sách cần cập nhật.");
        }
        s.setTheLoai(theLoaiRepo.findById(maTheLoai).orElseThrow(() -> new NghiepVuException("Thể loại không tồn tại.")));
        s.setNhaXuatBan(nxbRepo.findById(maNhaXuatBan).orElseThrow(() -> new NghiepVuException("Nhà xuất bản không tồn tại.")));
        dauSachRepo.save(s);
        return capNhat ? "Cập nhật đầu sách thành công." : "Thêm đầu sách thành công.";
    }

    public String xoa(String ma) {
        if (chiTietRepo.existsByDauSach_MaDauSach(ma)) {
            throw new NghiepVuException("Đầu sách đã có trong phiếu mượn, không thể xóa.");
        }
        dauSachRepo.deleteById(ma);
        return "Đã xóa đầu sách " + ma + ".";
    }
}

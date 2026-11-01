package com.thuvien.qltv.service;

import com.thuvien.qltv.domain.DocGia;
import com.thuvien.qltv.domain.TheDocGia;
import com.thuvien.qltv.repository.DocGiaRepository;
import com.thuvien.qltv.repository.PhieuMuonRepository;
import com.thuvien.qltv.repository.TheDocGiaRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Quản lý độc giả + cấp/gia hạn thẻ (tương ứng DocGiaService.cs). */
@Service
@Transactional
public class DocGiaService {

    private static final DateTimeFormatter VN = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DocGiaRepository docGiaRepo;
    private final TheDocGiaRepository theRepo;
    private final PhieuMuonRepository phieuMuonRepo;
    private final MaSinh maSinh;
    private final Clock clock;

    public DocGiaService(DocGiaRepository docGiaRepo, TheDocGiaRepository theRepo, PhieuMuonRepository phieuMuonRepo,
                         MaSinh maSinh, Clock clock) {
        this.docGiaRepo = docGiaRepo;
        this.theRepo = theRepo;
        this.phieuMuonRepo = phieuMuonRepo;
        this.maSinh = maSinh;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<DocGia> layDanhSach() {
        return docGiaRepo.findAllByOrderByMaDocGiaAsc();
    }

    @Transactional(readOnly = true)
    public DocGia tim(String ma) {
        return docGiaRepo.findById(ma).orElseThrow(() -> new NghiepVuException("Không tìm thấy độc giả " + ma + "."));
    }

    /** Thẻ đang hoạt động (TrangThai = 1) mới nhất của độc giả, nếu có. */
    @Transactional(readOnly = true)
    public Optional<TheDocGia> theHienTai(String maDocGia) {
        return theRepo.findTheHoatDong(maDocGia).stream().findFirst();
    }

    public String luu(DocGia dg, boolean capNhat) {
        if (dg == null) {
            throw new NghiepVuException("Dữ liệu độc giả không hợp lệ.");
        }
        KiemTra.batBuoc("Vui lòng nhập đầy đủ các trường bắt buộc của độc giả.",
                dg.getMaDocGia(), dg.getHo(), dg.getTen(), dg.getPhai(), dg.getDiaChi(), dg.getEmail());
        if (dg.getNgaySinh() == null) {
            throw new NghiepVuException("Vui lòng nhập ngày sinh độc giả.");
        }
        if (!dg.getEmail().contains("@")) {
            throw new NghiepVuException("Email không đúng định dạng cơ bản.");
        }
        dg.setMaDocGia(KiemTra.sach(dg.getMaDocGia()));
        boolean tonTai = docGiaRepo.existsById(dg.getMaDocGia());
        if (!capNhat && tonTai) {
            throw new NghiepVuException("Mã độc giả đã tồn tại.");
        }
        if (capNhat && !tonTai) {
            throw new NghiepVuException("Không tìm thấy độc giả cần cập nhật.");
        }
        docGiaRepo.save(dg);
        return capNhat ? "Cập nhật độc giả thành công." : "Thêm độc giả thành công.";
    }

    public String xoa(String ma) {
        if (theRepo.existsByDocGia_MaDocGia(ma) || phieuMuonRepo.existsByDocGia_MaDocGia(ma)) {
            throw new NghiepVuException("Độc giả đã có thẻ hoặc phiếu mượn, không thể xóa.");
        }
        docGiaRepo.deleteById(ma);
        return "Đã xóa độc giả " + ma + ".";
    }

    /** BR01–BR03: cấp thẻ mới, bảo đảm tại một thời điểm chỉ có một thẻ có giá trị. */
    public String capThe(String maDocGia, LocalDate ngayCap, LocalDate hanSuDung, boolean daDongLePhi) {
        if (ngayCap == null || hanSuDung == null) {
            throw new NghiepVuException("Vui lòng nhập ngày cấp và hạn sử dụng.");
        }
        if (hanSuDung.isBefore(ngayCap)) {
            throw new NghiepVuException("Hạn sử dụng phải từ ngày cấp trở đi.");
        }
        DocGia dg = tim(maDocGia);
        if (theRepo.demTheConGiaTri(maDocGia, ngayCap) > 0) {
            throw new NghiepVuException("Độc giả đang có một thẻ còn giá trị sử dụng.");
        }
        theRepo.voHieuTheCu(maDocGia);
        TheDocGia the = new TheDocGia();
        String maThe = maSinh.moi("THE" + maDocGia + "_");
        the.setMaThe(maThe.length() <= 30 ? maThe : maSinh.moi("THE"));
        the.setDocGia(dg);
        the.setNgayCap(ngayCap);
        the.setHanSuDung(hanSuDung);
        the.setDaDongLePhi(daDongLePhi);
        the.setTrangThai(true);
        theRepo.save(the);
        return "Cấp thẻ thư viện thành công. Mã thẻ: " + the.getMaThe();
    }

    public String giaHanThe(String maDocGia, LocalDate hanMoi, boolean daDongLePhi) {
        if (hanMoi == null) {
            throw new NghiepVuException("Vui lòng nhập hạn sử dụng mới.");
        }
        TheDocGia the = theHienTai(maDocGia)
                .orElseThrow(() -> new NghiepVuException("Độc giả chưa có thẻ đang hoạt động."));
        if (!hanMoi.isAfter(the.getHanSuDung())) {
            throw new NghiepVuException("Hạn sử dụng mới phải sau hạn sử dụng hiện tại.");
        }
        the.setHanSuDung(hanMoi);
        the.setDaDongLePhi(daDongLePhi);
        theRepo.save(the);
        return "Gia hạn thẻ thành công đến " + hanMoi.format(VN) + ".";
    }

    public LocalDate homNay() {
        return LocalDate.now(clock);
    }
}

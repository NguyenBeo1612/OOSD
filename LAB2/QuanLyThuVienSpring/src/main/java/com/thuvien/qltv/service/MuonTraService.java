package com.thuvien.qltv.service;

import com.thuvien.qltv.domain.ChiTietPhieuMuon;
import com.thuvien.qltv.domain.DauSach;
import com.thuvien.qltv.domain.DocGia;
import com.thuvien.qltv.domain.NhanVien;
import com.thuvien.qltv.domain.PhieuMuon;
import com.thuvien.qltv.domain.PhieuPhat;
import com.thuvien.qltv.domain.TheDocGia;
import com.thuvien.qltv.repository.ChiTietPhieuMuonRepository;
import com.thuvien.qltv.repository.DauSachRepository;
import com.thuvien.qltv.repository.DocGiaRepository;
import com.thuvien.qltv.repository.NhanVienRepository;
import com.thuvien.qltv.repository.PhieuMuonRepository;
import com.thuvien.qltv.repository.PhieuPhatRepository;
import com.thuvien.qltv.repository.TheDocGiaRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Nghiệp vụ mượn – trả – phạt (tương ứng MuonTraService.cs).
 * Business Rule được kiểm tra lại ở Service, không chỉ ở giao diện.
 */
@Service
public class MuonTraService {

    public static final int TOI_DA_SACH = 3;   // BR04

    private final TheDocGiaRepository theRepo;
    private final ChiTietPhieuMuonRepository chiTietRepo;
    private final PhieuMuonRepository phieuMuonRepo;
    private final DauSachRepository dauSachRepo;
    private final PhieuPhatRepository phieuPhatRepo;
    private final DocGiaRepository docGiaRepo;
    private final NhanVienRepository nhanVienRepo;
    private final MaSinh maSinh;
    private final Clock clock;

    public MuonTraService(TheDocGiaRepository theRepo, ChiTietPhieuMuonRepository chiTietRepo,
                          PhieuMuonRepository phieuMuonRepo, DauSachRepository dauSachRepo,
                          PhieuPhatRepository phieuPhatRepo, DocGiaRepository docGiaRepo,
                          NhanVienRepository nhanVienRepo, MaSinh maSinh, Clock clock) {
        this.theRepo = theRepo;
        this.chiTietRepo = chiTietRepo;
        this.phieuMuonRepo = phieuMuonRepo;
        this.dauSachRepo = dauSachRepo;
        this.phieuPhatRepo = phieuPhatRepo;
        this.docGiaRepo = docGiaRepo;
        this.nhanVienRepo = nhanVienRepo;
        this.maSinh = maSinh;
        this.clock = clock;
    }

    /** UC_QLTV_006 – Kiểm tra điều kiện mượn: BR01, BR03, BR04, BR05. */
    @Transactional(readOnly = true)
    public String kiemTraDieuKienMuon(String maDocGia, int soSachMuonMoi) {
        if (KiemTra.rong(maDocGia)) {
            throw new NghiepVuException("Vui lòng chọn độc giả.");
        }
        if (soSachMuonMoi < 0) {
            throw new NghiepVuException("Số sách mượn mới không hợp lệ.");
        }
        if (soSachMuonMoi > TOI_DA_SACH) {
            throw new NghiepVuException("Một lần lập phiếu chỉ được chọn tối đa 3 đầu sách khác nhau.");
        }
        LocalDate homNay = LocalDate.now(clock);
        List<TheDocGia> the = theRepo.findTheHoatDong(maDocGia);
        if (the.isEmpty()) {
            throw new NghiepVuException("Độc giả chưa có thẻ thư viện đang hoạt động.");                    // BR01
        }
        TheDocGia t = the.get(0);
        if (t.getHanSuDung().isBefore(homNay)) {
            throw new NghiepVuException("Thẻ thư viện đã hết hạn.");                                          // BR03
        }
        if (!Boolean.TRUE.equals(t.getDaDongLePhi())) {
            throw new NghiepVuException("Độc giả chưa đóng lệ phí năm nên thẻ chưa có giá trị.");             // BR03
        }
        if (chiTietRepo.demQuaHan(maDocGia, homNay) > 0) {
            throw new NghiepVuException("Độc giả còn sách quá hạn chưa trả nên không được mượn thêm.");       // BR05
        }
        long dangMuon = chiTietRepo.demDangMuon(maDocGia);
        if (dangMuon + soSachMuonMoi > TOI_DA_SACH) {
            throw new NghiepVuException("Tổng số sách đang mượn (" + dangMuon + ") và sắp mượn ("
                    + soSachMuonMoi + ") không được vượt quá 3 cuốn.");                                     // BR04
        }
        return "Độc giả đủ điều kiện mượn sách (đang mượn " + dangMuon + "/3 cuốn).";
    }

    /** UC_QLTV_003 – Lập phiếu mượn trong một transaction SERIALIZABLE. */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String lapPhieuMuon(String maDocGia, String maNhanVien, Collection<String> maDauSach,
                               LocalDate ngayMuon, LocalDate ngayHenTra) {
        if (maDauSach == null) {
            throw new NghiepVuException("Danh sách sách mượn không hợp lệ.");
        }
        Set<String> unique = new LinkedHashSet<>();                                 // BR06: không trùng đầu sách
        for (String ma : maDauSach) {
            if (!KiemTra.rong(ma)) {
                unique.add(ma.trim());
            }
        }
        if (unique.isEmpty()) {
            throw new NghiepVuException("Phải chọn ít nhất 1 đầu sách.");
        }
        kiemTraDieuKienMuon(maDocGia, unique.size());
        if (KiemTra.rong(maNhanVien)) {
            throw new NghiepVuException("Vui lòng chọn nhân viên lập phiếu.");
        }
        if (ngayMuon == null || ngayHenTra == null) {
            throw new NghiepVuException("Vui lòng nhập ngày mượn và ngày hẹn trả.");
        }
        if (ngayHenTra.isBefore(ngayMuon)) {
            throw new NghiepVuException("Ngày hẹn trả không được trước ngày mượn.");                          // BR08
        }
        DocGia dg = docGiaRepo.findById(maDocGia).orElseThrow(() -> new NghiepVuException("Không tìm thấy độc giả."));
        NhanVien nv = nhanVienRepo.findById(maNhanVien).orElseThrow(() -> new NghiepVuException("Không tìm thấy nhân viên."));

        List<DauSach> sach = new ArrayList<>();
        for (String ma : unique) {                                                                            // BR07
            DauSach s = dauSachRepo.findByIdForUpdate(ma)
                    .orElseThrow(() -> new NghiepVuException("Không tìm thấy đầu sách " + ma + "."));
            if (s.getSoLuongHienCo() == null || s.getSoLuongHienCo() <= 0) {
                throw new NghiepVuException("Đầu sách " + ma + " đã hết trong kho.");
            }
            sach.add(s);
        }

        PhieuMuon pm = new PhieuMuon();
        pm.setMaPhieuMuon(maSinh.moi("PM"));
        pm.setDocGia(dg);
        pm.setNhanVien(nv);
        pm.setNgayMuon(ngayMuon);
        pm.setNgayHenTra(ngayHenTra);
        int i = 1;
        for (DauSach s : sach) {
            ChiTietPhieuMuon ct = new ChiTietPhieuMuon();
            ct.setMaChiTiet(pm.getMaPhieuMuon() + "_" + String.format("%02d", i++));
            ct.setDauSach(s);
            pm.themChiTiet(ct);
            s.giamTon();
        }
        phieuMuonRepo.save(pm);
        dauSachRepo.saveAll(sach);
        return "Lập phiếu mượn " + pm.getMaPhieuMuon() + " thành công (" + sach.size() + " cuốn).";
    }

    @Transactional(readOnly = true)
    public List<ChiTietPhieuMuon> laySachDangMuon(String maDocGia) {
        return KiemTra.rong(maDocGia) ? List.of() : chiTietRepo.findDangMuon(maDocGia);
    }

    /** UC_QLTV_007 + UC_QLTV_008 – Nhận trả sách, lập phiếu phạt nếu trễ / mất / hư (BR09, BR10). */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String traSach(String maChiTiet, String maNhanVien, LocalDate ngayTra, String tinhTrang, BigDecimal phiPhat) {
        if (KiemTra.rong(maChiTiet)) {
            throw new NghiepVuException("Vui lòng chọn sách cần trả.");
        }
        if (KiemTra.rong(maNhanVien)) {
            throw new NghiepVuException("Vui lòng chọn nhân viên nhận trả.");
        }
        if (KiemTra.rong(tinhTrang) || !TinhTrangSach.TAT_CA.contains(tinhTrang.trim())) {
            throw new NghiepVuException("Vui lòng chọn tình trạng sách.");
        }
        if (ngayTra == null) {
            throw new NghiepVuException("Vui lòng nhập ngày trả.");
        }
        ChiTietPhieuMuon ct = chiTietRepo.findById(maChiTiet)
                .orElseThrow(() -> new NghiepVuException("Không tìm thấy chi tiết mượn."));
        if (ct.daTra()) {
            throw new NghiepVuException("Sách này đã được trả trước đó.");
        }
        PhieuMuon pm = ct.getPhieuMuon();
        if (ngayTra.isBefore(pm.getNgayMuon())) {
            throw new NghiepVuException("Ngày trả không thể trước ngày mượn.");
        }
        NhanVien nv = nhanVienRepo.findById(maNhanVien).orElseThrow(() -> new NghiepVuException("Không tìm thấy nhân viên."));
        String tt = tinhTrang.trim();
        boolean quaHan = ngayTra.isAfter(pm.getNgayHenTra());
        boolean mat = TinhTrangSach.MAT.equals(tt);
        boolean huHong = TinhTrangSach.RACH_HU.equals(tt);
        boolean canPhat = quaHan || mat || huHong;
        BigDecimal phi = phiPhat == null ? BigDecimal.ZERO : phiPhat;
        if (phi.signum() < 0) {
            throw new NghiepVuException("Phí phạt không được âm.");
        }
        if (canPhat && phi.signum() <= 0) {
            throw new NghiepVuException("Trường hợp trả trễ/mất/hư hỏng phải nhập phí phạt cụ thể lớn hơn 0.");
        }

        ct.setNgayTraThucTe(ngayTra);
        ct.setTinhTrangTra(tt);
        chiTietRepo.save(ct);

        // Sách mất hoặc hư hỏng không còn sẵn sàng cho mượn nên không cộng lại tồn kho.
        if (!mat && !huHong) {
            DauSach s = dauSachRepo.findByIdForUpdate(ct.getDauSach().getMaDauSach())
                    .orElseThrow(() -> new NghiepVuException("Không tìm thấy đầu sách."));
            s.tangTon();
            dauSachRepo.save(s);
        }

        if (!canPhat) {
            return "Trả sách thành công.";
        }
        List<String> lyDo = new ArrayList<>();
        if (quaHan) {
            lyDo.add("Trả trễ hạn");
        }
        if (mat) {
            lyDo.add("Mất sách");
        }
        if (huHong) {
            lyDo.add("Rách/hư hỏng");
        }
        PhieuPhat pp = new PhieuPhat();
        pp.setMaPhieuPhat(maSinh.moi("PP"));
        pp.setChiTiet(ct);
        pp.setNhanVien(nv);
        pp.setNgayPhat(ngayTra);
        pp.setLyDo(String.join(", ", lyDo));
        pp.setPhiPhat(phi);
        phieuPhatRepo.save(pp);
        return "Trả sách thành công và đã lập phiếu phạt " + pp.getMaPhieuPhat() + " (" + pp.getLyDo() + ").";
    }
}

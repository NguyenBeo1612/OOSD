package com.thuvien.qltv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thuvien.qltv.domain.ChiTietPhieuMuon;
import com.thuvien.qltv.domain.DauSach;
import com.thuvien.qltv.domain.DocGia;
import com.thuvien.qltv.domain.PhieuPhat;
import com.thuvien.qltv.domain.TheDocGia;
import com.thuvien.qltv.repository.DauSachRepository;
import com.thuvien.qltv.repository.DocGiaRepository;
import com.thuvien.qltv.repository.NhaXuatBanRepository;
import com.thuvien.qltv.repository.PhieuPhatRepository;
import com.thuvien.qltv.repository.TheDocGiaRepository;
import com.thuvien.qltv.repository.TheLoaiRepository;
import com.thuvien.qltv.service.DocGiaService;
import com.thuvien.qltv.service.MuonTraService;
import com.thuvien.qltv.service.NghiepVuException;
import com.thuvien.qltv.service.ThongKeService;
import com.thuvien.qltv.service.ThongKeTongHop;
import com.thuvien.qltv.service.TinhTrangSach;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * 14 test case bắt buộc của LAB 2 (mục 9). Chạy trên H2 (profile demo), mỗi test tự rollback.
 * Lệnh chạy: mvn test
 */
@SpringBootTest
@ActiveProfiles("demo")
@Transactional
class NghiepVuTest {

    private static final String NV = "NV001";

    @Autowired DocGiaService docGiaService;
    @Autowired MuonTraService muonTraService;
    @Autowired ThongKeService thongKeService;
    @Autowired DocGiaRepository docGiaRepo;
    @Autowired TheDocGiaRepository theRepo;
    @Autowired DauSachRepository dauSachRepo;
    @Autowired TheLoaiRepository theLoaiRepo;
    @Autowired NhaXuatBanRepository nxbRepo;
    @Autowired PhieuPhatRepository phieuPhatRepo;

    private LocalDate homNay;

    @BeforeEach
    void setUp() {
        homNay = docGiaService.homNay();
    }

    // ------------------------------------------------ helpers
    private DocGia docGia(String ma) {
        DocGia d = new DocGia();
        d.setMaDocGia(ma);
        d.setHo("Test");
        d.setTen(ma);
        d.setNgaySinh(LocalDate.of(2003, 1, 1));
        d.setPhai("Nam");
        d.setDiaChi("TP.HCM");
        d.setEmail(ma.toLowerCase() + "@test.vn");
        return docGiaRepo.save(d);
    }

    private DocGia docGiaCoThe(String ma, LocalDate han, boolean lePhi) {
        DocGia d = docGia(ma);
        TheDocGia t = new TheDocGia();
        t.setMaThe("T_" + ma);
        t.setDocGia(d);
        t.setNgayCap(han.minusYears(1));
        t.setHanSuDung(han);
        t.setDaDongLePhi(lePhi);
        t.setTrangThai(true);
        theRepo.save(t);
        return d;
    }

    private DauSach sach(String ma, int soLuong) {
        DauSach s = new DauSach();
        s.setMaDauSach(ma);
        s.setTenSach("Sách " + ma);
        s.setNamXuatBan(2024);
        s.setSoLuongHienCo(soLuong);
        s.setTheLoai(theLoaiRepo.findById("TL001").orElseThrow());
        s.setNhaXuatBan(nxbRepo.findById("NXB001").orElseThrow());
        return dauSachRepo.save(s);
    }

    private int ton(String maSach) {
        return dauSachRepo.findById(maSach).orElseThrow().getSoLuongHienCo();
    }

    private ChiTietPhieuMuon muonMot(String maDG, String maSach, LocalDate ngayMuon, LocalDate henTra) {
        muonTraService.lapPhieuMuon(maDG, NV, List.of(maSach), ngayMuon, henTra);
        return muonTraService.laySachDangMuon(maDG).stream()
                .filter(c -> c.getDauSach().getMaDauSach().equals(maSach)).findFirst().orElseThrow();
    }

    // ------------------------------------------------ test cases
    @Test
    @DisplayName("TC01 – Cấp thẻ cho độc giả chưa có thẻ hoạt động → tạo thẻ mới")
    void tc01() {
        docGia("TDG01");
        String kq = docGiaService.capThe("TDG01", homNay, homNay.plusYears(1), true);
        assertTrue(kq.startsWith("Cấp thẻ thư viện thành công"));
        assertTrue(docGiaService.theHienTai("TDG01").isPresent());
    }

    @Test
    @DisplayName("TC02 – Cấp thẻ khi đang có thẻ còn hạn → từ chối cấp thẻ thứ hai")
    void tc02() {
        docGiaCoThe("TDG02", homNay.plusMonths(6), true);
        NghiepVuException ex = assertThrows(NghiepVuException.class,
                () -> docGiaService.capThe("TDG02", homNay, homNay.plusYears(1), true));
        assertTrue(ex.getMessage().contains("còn giá trị"));
    }

    @Test
    @DisplayName("TC03 – Mượn khi thẻ hết hạn → từ chối")
    void tc03() {
        docGiaCoThe("TDG03", homNay.minusDays(1), true);
        NghiepVuException ex = assertThrows(NghiepVuException.class,
                () -> muonTraService.kiemTraDieuKienMuon("TDG03", 1));
        assertTrue(ex.getMessage().contains("hết hạn"));
    }

    @Test
    @DisplayName("TC04 – Mượn khi chưa đóng lệ phí → từ chối")
    void tc04() {
        docGiaCoThe("TDG04", homNay.plusMonths(6), false);
        NghiepVuException ex = assertThrows(NghiepVuException.class,
                () -> muonTraService.kiemTraDieuKienMuon("TDG04", 1));
        assertTrue(ex.getMessage().contains("lệ phí"));
    }

    @Test
    @DisplayName("TC05 – Mượn khi còn sách quá hạn → từ chối")
    void tc05() {
        docGiaCoThe("TDG05", homNay.plusMonths(6), true);
        sach("TS05A", 3);
        muonMot("TDG05", "TS05A", homNay.minusDays(20), homNay.minusDays(5));
        NghiepVuException ex = assertThrows(NghiepVuException.class,
                () -> muonTraService.kiemTraDieuKienMuon("TDG05", 1));
        assertTrue(ex.getMessage().contains("quá hạn"));
    }

    @Test
    @DisplayName("TC06 – Đang mượn 2 + chọn thêm 2 → từ chối vì tổng > 3")
    void tc06() {
        docGiaCoThe("TDG06", homNay.plusMonths(6), true);
        sach("TS06A", 3);
        sach("TS06B", 3);
        sach("TS06C", 3);
        sach("TS06D", 3);
        muonTraService.lapPhieuMuon("TDG06", NV, List.of("TS06A", "TS06B"), homNay, homNay.plusDays(14));
        NghiepVuException ex = assertThrows(NghiepVuException.class,
                () -> muonTraService.lapPhieuMuon("TDG06", NV, List.of("TS06C", "TS06D"), homNay, homNay.plusDays(14)));
        assertTrue(ex.getMessage().contains("không được vượt quá 3"));
    }

    @Test
    @DisplayName("TC07 – Chọn 2 đầu sách hợp lệ → tạo phiếu, giảm tồn kho 2")
    void tc07() {
        docGiaCoThe("TDG07", homNay.plusMonths(6), true);
        sach("TS07A", 5);
        sach("TS07B", 4);
        String kq = muonTraService.lapPhieuMuon("TDG07", NV, List.of("TS07A", "TS07B"), homNay, homNay.plusDays(14));
        assertTrue(kq.startsWith("Lập phiếu mượn PM"));
        assertEquals(4, ton("TS07A"));
        assertEquals(3, ton("TS07B"));
        assertEquals(2, muonTraService.laySachDangMuon("TDG07").size());
    }

    @Test
    @DisplayName("TC08 – Sách có SoLuongHienCo = 0 → từ chối, không lập phiếu")
    void tc08() {
        docGiaCoThe("TDG08", homNay.plusMonths(6), true);
        sach("TS08A", 0);
        NghiepVuException ex = assertThrows(NghiepVuException.class,
                () -> muonTraService.lapPhieuMuon("TDG08", NV, List.of("TS08A"), homNay, homNay.plusDays(14)));
        assertTrue(ex.getMessage().contains("đã hết"));
        assertEquals(0, ton("TS08A"));
        assertEquals(0, muonTraService.laySachDangMuon("TDG08").size());
    }

    @Test
    @DisplayName("TC09 – Trả đúng hạn + Bình thường → cập nhật trả, tăng tồn kho, không phạt")
    void tc09() {
        docGiaCoThe("TDG09", homNay.plusMonths(6), true);
        sach("TS09A", 2);
        ChiTietPhieuMuon ct = muonMot("TDG09", "TS09A", homNay, homNay.plusDays(14));
        assertEquals(1, ton("TS09A"));
        String kq = muonTraService.traSach(ct.getMaChiTiet(), NV, homNay, TinhTrangSach.BINH_THUONG, BigDecimal.ZERO);
        assertEquals("Trả sách thành công.", kq);
        assertEquals(2, ton("TS09A"));
        assertTrue(phieuPhatRepo.findByChiTiet_MaChiTiet(ct.getMaChiTiet()).isEmpty());
    }

    @Test
    @DisplayName("TC10 – Trả trễ hạn + phí = 0 → yêu cầu phí phạt > 0")
    void tc10() {
        docGiaCoThe("TDG10", homNay.plusMonths(6), true);
        sach("TS10A", 2);
        ChiTietPhieuMuon ct = muonMot("TDG10", "TS10A", homNay.minusDays(20), homNay.minusDays(6));
        NghiepVuException ex = assertThrows(NghiepVuException.class,
                () -> muonTraService.traSach(ct.getMaChiTiet(), NV, homNay, TinhTrangSach.BINH_THUONG, BigDecimal.ZERO));
        assertTrue(ex.getMessage().contains("lớn hơn 0"));
    }

    @Test
    @DisplayName("TC11 – Trả trễ hạn + phí > 0 → tạo phiếu phạt")
    void tc11() {
        docGiaCoThe("TDG11", homNay.plusMonths(6), true);
        sach("TS11A", 2);
        ChiTietPhieuMuon ct = muonMot("TDG11", "TS11A", homNay.minusDays(20), homNay.minusDays(6));
        muonTraService.traSach(ct.getMaChiTiet(), NV, homNay, TinhTrangSach.BINH_THUONG, new BigDecimal("12000"));
        List<PhieuPhat> pp = phieuPhatRepo.findByChiTiet_MaChiTiet(ct.getMaChiTiet());
        assertEquals(1, pp.size());
        assertEquals("Trả trễ hạn", pp.get(0).getLyDo());
        assertEquals(2, ton("TS11A"));
    }

    @Test
    @DisplayName("TC12 – Trả Mất + phí > 0 → tạo phạt, không tăng tồn kho")
    void tc12() {
        docGiaCoThe("TDG12", homNay.plusMonths(6), true);
        sach("TS12A", 2);
        ChiTietPhieuMuon ct = muonMot("TDG12", "TS12A", homNay, homNay.plusDays(14));
        muonTraService.traSach(ct.getMaChiTiet(), NV, homNay, TinhTrangSach.MAT, new BigDecimal("150000"));
        assertEquals(1, phieuPhatRepo.findByChiTiet_MaChiTiet(ct.getMaChiTiet()).size());
        assertEquals(1, ton("TS12A"));
    }

    @Test
    @DisplayName("TC13 – Trả Rách/Hư + phí > 0 → tạo phạt, không tăng tồn kho")
    void tc13() {
        docGiaCoThe("TDG13", homNay.plusMonths(6), true);
        sach("TS13A", 2);
        ChiTietPhieuMuon ct = muonMot("TDG13", "TS13A", homNay, homNay.plusDays(14));
        muonTraService.traSach(ct.getMaChiTiet(), NV, homNay, TinhTrangSach.RACH_HU, new BigDecimal("50000"));
        assertEquals("Rách/hư hỏng", phieuPhatRepo.findByChiTiet_MaChiTiet(ct.getMaChiTiet()).get(0).getLyDo());
        assertEquals(1, ton("TS13A"));
    }

    @Test
    @DisplayName("TC14 – Thống kê khoảng có giao dịch → số liệu mượn/mất/hư/quá hạn/phạt đúng")
    void tc14() {
        LocalDate tu = homNay.minusDays(30);
        ThongKeTongHop truoc = thongKeService.layTongHop(tu, homNay);

        docGiaCoThe("TDG14", homNay.plusMonths(6), true);
        sach("TS14A", 3);
        sach("TS14B", 3);
        sach("TS14C", 3);
        ChiTietPhieuMuon b = muonMot("TDG14", "TS14B", homNay.minusDays(3), homNay.plusDays(10));
        ChiTietPhieuMuon c = muonMot("TDG14", "TS14C", homNay.minusDays(3), homNay.plusDays(10));
        // phiếu quá hạn lập sau cùng (nếu lập trước, BR05 sẽ chặn hai phiếu sau)
        ChiTietPhieuMuon a = muonMot("TDG14", "TS14A", homNay.minusDays(20), homNay.minusDays(6));
        muonTraService.traSach(a.getMaChiTiet(), NV, homNay, TinhTrangSach.BINH_THUONG, new BigDecimal("10000"));
        muonTraService.traSach(b.getMaChiTiet(), NV, homNay, TinhTrangSach.MAT, new BigDecimal("100000"));
        muonTraService.traSach(c.getMaChiTiet(), NV, homNay, TinhTrangSach.RACH_HU, new BigDecimal("30000"));

        // nhập ngược mốc thời gian: service tự đổi thứ tự
        ThongKeTongHop sau = thongKeService.layTongHop(homNay, tu);
        assertEquals(truoc.luotSachMuon() + 3, sau.luotSachMuon());
        assertEquals(truoc.sachQuaHan() + 1, sau.sachQuaHan());
        assertEquals(truoc.sachMat() + 1, sau.sachMat());
        assertEquals(truoc.sachHuHong() + 1, sau.sachHuHong());
        assertEquals(0, truoc.tongPhiPhat().add(new BigDecimal("140000")).compareTo(sau.tongPhiPhat()));
    }
}

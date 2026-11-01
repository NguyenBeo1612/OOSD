package com.khachsan.qlks;

import static com.khachsan.qlks.service.Db.p;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.khachsan.qlks.service.Db;
import com.khachsan.qlks.service.DatPhongService;
import com.khachsan.qlks.service.DenBuItem;
import com.khachsan.qlks.service.DichVuService;
import com.khachsan.qlks.service.NghiepVuException;
import com.khachsan.qlks.service.PhongDat;
import com.khachsan.qlks.service.PhongTienNghiService;
import com.khachsan.qlks.service.ThongKeService;
import com.khachsan.qlks.service.TraPhongService;
import com.khachsan.qlks.service.TrangThai;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * 15 test case tổng thể của LAB 3 (mục 9). Chạy trên H2 (profile demo), mỗi test tự rollback.
 * Lệnh chạy: mvn test
 */
@SpringBootTest
@ActiveProfiles("demo")
@Transactional
class NghiepVuTest {

    private static final String LE_TAN = "NV01";
    private static final String PHUC_VU = "NV02";
    private static final String THU_NGAN = "NV03";
    private static final LocalDate HOM_NAY = LocalDate.now();

    @Autowired Db db;
    @Autowired PhongTienNghiService phongService;
    @Autowired DatPhongService datPhongService;
    @Autowired DichVuService dichVuService;
    @Autowired TraPhongService traPhongService;
    @Autowired ThongKeService thongKeService;

    // ------------------------------------------------ helpers
    private String phong(String so, int sucChua, int gia) {
        phongService.themPhong(so, "A", sucChua, BigDecimal.valueOf(gia));
        return so;
    }

    /** Lập phiếu 1 phòng, trả về số phiếu. */
    private String dat(String soPhong, int soNguoi, LocalDate nhan, LocalDate tra) {
        String kq = datPhongService.taoDatPhong("KH01", LE_TAN, nhan, tra, BigDecimal.ZERO, "Trực tiếp",
                List.of(new PhongDat(soPhong, soNguoi)));
        return kq.substring(kq.indexOf("DP"), kq.length() - 1);
    }

    private String dangO(String soPhong, int gia) {
        phong(soPhong, 2, gia);
        String so = dat(soPhong, 2, HOM_NAY, HOM_NAY.plusDays(2));
        datPhongService.nhanPhong(so, LocalDateTime.now());
        return so;
    }

    private String trangThaiPhieu(String so) {
        return db.scalar("SELECT TrangThai FROM PhieuDatPhong WHERE SoPhieuDat = :s", p("s", so), String.class);
    }

    // ------------------------------------------------ test cases
    @Test
    @DisplayName("TC01 – Phòng trống, số người ≤ sức chứa → lập phiếu thành công")
    void tc01() {
        phong("T101", 2, 500000);
        String so = dat("T101", 2, HOM_NAY, HOM_NAY.plusDays(1));
        assertEquals(TrangThai.DP_DA_DAT, trangThaiPhieu(so));
        assertEquals(TrangThai.PHONG_DA_DAT, db.scalar("SELECT TrangThai FROM Phong WHERE SoPhong = 'T101'", p(), String.class));
    }

    @Test
    @DisplayName("TC02 – Số người vượt sức chứa → từ chối")
    void tc02() {
        phong("T102", 2, 500000);
        NghiepVuException ex = assertThrows(NghiepVuException.class, () -> dat("T102", 3, HOM_NAY, HOM_NAY.plusDays(1)));
        assertTrue(ex.getMessage().contains("vượt sức chứa"));
    }

    @Test
    @DisplayName("TC03 – Phòng trùng lịch → từ chối")
    void tc03() {
        phong("T103", 2, 500000);
        dat("T103", 1, HOM_NAY.plusDays(5), HOM_NAY.plusDays(8));
        NghiepVuException ex = assertThrows(NghiepVuException.class, () -> dat("T103", 1, HOM_NAY.plusDays(7), HOM_NAY.plusDays(9)));
        assertTrue(ex.getMessage().contains("trùng lịch"));
    }

    @Test
    @DisplayName("TC04 – Thiết bị đã lắp trong cùng ngày → từ chối lập phiếu lắp đặt")
    void tc04() {
        phong("T104", 2, 500000);
        phongService.lapDat("TV01", "T104", HOM_NAY, "Tốt", PHUC_VU, null);
        NghiepVuException ex = assertThrows(NghiepVuException.class,
                () -> phongService.lapDat("TV01", "A101", HOM_NAY, "Tốt", PHUC_VU, null));
        assertTrue(ex.getMessage().contains("đã được lắp"));
    }

    @Test
    @DisplayName("TC05 – Phiếu Đã đặt → nhận phòng → Đang ở")
    void tc05() {
        phong("T105", 2, 500000);
        String so = dat("T105", 2, HOM_NAY, HOM_NAY.plusDays(1));
        datPhongService.nhanPhong(so, LocalDateTime.now());
        assertEquals(TrangThai.DP_DANG_O, trangThaiPhieu(so));
    }

    @Test
    @DisplayName("TC06 – Đã đủ số người → thêm người lưu trú bị từ chối")
    void tc06() {
        phong("T106", 2, 500000);
        String so = dat("T106", 1, HOM_NAY, HOM_NAY.plusDays(1));
        datPhongService.themNguoiLuuTru(so, "T106", "Nguyễn Văn A", "079000000111", "Việt Nam");
        NghiepVuException ex = assertThrows(NghiepVuException.class,
                () -> datPhongService.themNguoiLuuTru(so, "T106", "Trần Thị B", "079000000222", "Việt Nam"));
        assertTrue(ex.getMessage().contains("Đã đủ số người"));
    }

    @Test
    @DisplayName("TC07 – Cùng dịch vụ cùng ngày ghi lần 2 → cộng dồn")
    void tc07() {
        String so = dangO("T107", 500000);
        dichVuService.ghiNhan(so, "T107", HOM_NAY, LE_TAN, "DV01", 2);
        dichVuService.ghiNhan(so, "T107", HOM_NAY, LE_TAN, "DV01", 3);
        List<Map<String, Object>> ls = dichVuService.layLichSu(so);
        assertEquals(1, ls.size());
        assertEquals(5, ((Number) ls.get(0).get("SoLuong")).intValue());
    }

    @Test
    @DisplayName("TC08 – Phiếu không Đang ở → ghi dịch vụ bị từ chối")
    void tc08() {
        phong("T108", 2, 500000);
        String so = dat("T108", 1, HOM_NAY, HOM_NAY.plusDays(1));
        NghiepVuException ex = assertThrows(NghiepVuException.class,
                () -> dichVuService.ghiNhan(so, "T108", HOM_NAY, LE_TAN, "DV01", 1));
        assertTrue(ex.getMessage().contains("đang lưu trú"));
    }

    @Test
    @DisplayName("TC09 – Có hư hỏng → lập phiếu đền bù thành công")
    void tc09() {
        String so = dangO("T109", 500000);
        String kq = traPhongService.lapPhieuDenBu(so, "T109", PHUC_VU, List.of(new DenBuItem("TV02", "QD01")));
        assertTrue(kq.startsWith("Đã lập phiếu đền bù"));
        assertEquals(0, new BigDecimal("500000").compareTo(
                db.money("SELECT TongTien FROM PhieuDenBu WHERE SoPhieuDat = :s", p("s", so))));
    }

    @Test
    @DisplayName("TC10 – Phiếu đang ở → lập hóa đơn = tiền phòng + dịch vụ")
    void tc10() {
        String so = dangO("T110", 600000);
        dichVuService.ghiNhan(so, "T110", HOM_NAY, LE_TAN, "DV01", 2);   // 2 x 120.000
        traPhongService.lapHoaDon(so, THU_NGAN, 2);                       // 2 x 600.000
        Map<String, Object> hd = traPhongService.layHoaDon(so);
        assertEquals(0, new BigDecimal("1200000").compareTo((BigDecimal) hd.get("TienPhong")));
        assertEquals(0, new BigDecimal("240000").compareTo((BigDecimal) hd.get("TienDichVu")));
        assertEquals(0, new BigDecimal("1440000").compareTo((BigDecimal) hd.get("TongTien")));
    }

    @Test
    @DisplayName("TC11 – Thanh toán một phần → hóa đơn chưa đủ")
    void tc11() {
        String so = dangO("T111", 500000);
        traPhongService.lapHoaDon(so, THU_NGAN, 2);
        String soHD = (String) traPhongService.layHoaDon(so).get("SoHoaDon");
        traPhongService.thanhToan(soHD, "Tiền mặt", new BigDecimal("400000"));
        assertEquals(TrangThai.HD_CHUA_TT, traPhongService.layHoaDon(so).get("TrangThai"));
    }

    @Test
    @DisplayName("TC12 – Thanh toán đủ (nhiều hình thức) → hóa đơn đã thanh toán")
    void tc12() {
        String so = dangO("T112", 500000);
        traPhongService.lapHoaDon(so, THU_NGAN, 2);
        String soHD = (String) traPhongService.layHoaDon(so).get("SoHoaDon");
        traPhongService.thanhToan(soHD, "Tiền mặt", new BigDecimal("400000"));
        traPhongService.thanhToan(soHD, "Chuyển khoản", new BigDecimal("600000"));
        assertEquals(TrangThai.HD_DA_TT, traPhongService.layHoaDon(so).get("TrangThai"));
        assertThrows(NghiepVuException.class, () -> traPhongService.thanhToan(soHD, "Thẻ", BigDecimal.ONE));
    }

    @Test
    @DisplayName("TC13 – Hóa đơn chưa đủ → trả phòng bị từ chối")
    void tc13() {
        String so = dangO("T113", 500000);
        traPhongService.lapHoaDon(so, THU_NGAN, 1);
        NghiepVuException ex = assertThrows(NghiepVuException.class, () -> traPhongService.traPhong(so, LocalDateTime.now()));
        assertTrue(ex.getMessage().contains("chưa thanh toán đủ"));
    }

    @Test
    @DisplayName("TC14 – Hóa đơn đã đủ → trả phòng, giải phóng phòng")
    void tc14() {
        String so = dangO("T114", 500000);
        traPhongService.lapHoaDon(so, THU_NGAN, 1);
        String soHD = (String) traPhongService.layHoaDon(so).get("SoHoaDon");
        traPhongService.thanhToan(soHD, "Ví điện tử", new BigDecimal("500000"));
        traPhongService.traPhong(so, LocalDateTime.now());
        assertEquals(TrangThai.DP_DA_TRA, trangThaiPhieu(so));
        assertEquals(TrangThai.PHONG_TRONG, db.scalar("SELECT TrangThai FROM Phong WHERE SoPhong = 'T114'", p(), String.class));
    }

    @Test
    @DisplayName("TC15 – Khoảng ngày hợp lệ → có số liệu tổng hợp")
    void tc15() {
        Map<String, Object> truoc = thongKeService.tongHop(HOM_NAY.minusDays(1), HOM_NAY.plusDays(1));
        String so = dangO("T115", 500000);
        dichVuService.ghiNhan(so, "T115", HOM_NAY, LE_TAN, "DV02", 1);
        traPhongService.lapHoaDon(so, THU_NGAN, 1);
        Map<String, Object> sau = thongKeService.tongHop(HOM_NAY.minusDays(1), HOM_NAY.plusDays(1));
        assertEquals(((Number) truoc.get("SoPhieuDat")).longValue() + 1, ((Number) sau.get("SoPhieuDat")).longValue());
        assertEquals(((Number) truoc.get("SoHoaDon")).longValue() + 1, ((Number) sau.get("SoHoaDon")).longValue());
        assertTrue(thongKeService.dichVu(HOM_NAY, HOM_NAY).stream().anyMatch(r -> "DV02".equals(r.get("MaDV"))));
        assertThrows(NghiepVuException.class, () -> thongKeService.tongHop(HOM_NAY, HOM_NAY.minusDays(1)));
    }
}

package com.thuvien.qltv.service;

import com.thuvien.qltv.domain.PhieuPhat;
import com.thuvien.qltv.repository.ChiTietPhieuMuonRepository;
import com.thuvien.qltv.repository.PhieuPhatRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Thống kê mượn / quá hạn / mất / hư hỏng / phạt (tương ứng ThongKeService.cs). */
@Service
@Transactional(readOnly = true)
public class ThongKeService {

    private final ChiTietPhieuMuonRepository chiTietRepo;
    private final PhieuPhatRepository phieuPhatRepo;
    private final Clock clock;

    public ThongKeService(ChiTietPhieuMuonRepository chiTietRepo, PhieuPhatRepository phieuPhatRepo, Clock clock) {
        this.chiTietRepo = chiTietRepo;
        this.phieuPhatRepo = phieuPhatRepo;
        this.clock = clock;
    }

    public ThongKeTongHop layTongHop(LocalDate tuNgay, LocalDate denNgay) {
        LocalDate tu = tuNgay;
        LocalDate den = denNgay;
        if (den.isBefore(tu)) {          // đổi thứ tự nếu người dùng nhập ngược
            LocalDate t = tu;
            tu = den;
            den = t;
        }
        LocalDate homNay = LocalDate.now(clock);
        BigDecimal tongPhat = phieuPhatRepo.tongPhiPhat(tu, den);
        return new ThongKeTongHop(tu, den,
                chiTietRepo.demLuotMuon(tu, den),
                chiTietRepo.demQuaHanTrongKy(tu, den, homNay),
                chiTietRepo.demTheoTinhTrang(tu, den, TinhTrangSach.MAT),
                chiTietRepo.demTheoTinhTrang(tu, den, TinhTrangSach.RACH_HU),
                tongPhat == null ? BigDecimal.ZERO : tongPhat);
    }

    public List<PhieuPhat> layChiTietPhat(LocalDate tuNgay, LocalDate denNgay) {
        LocalDate tu = denNgay.isBefore(tuNgay) ? denNgay : tuNgay;
        LocalDate den = denNgay.isBefore(tuNgay) ? tuNgay : denNgay;
        return phieuPhatRepo.chiTietPhat(tu, den);
    }
}

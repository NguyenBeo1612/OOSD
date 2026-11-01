package com.thuvien.qltv.service;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Kết quả thống kê theo khoảng thời gian (BR11). */
public record ThongKeTongHop(LocalDate tuNgay, LocalDate denNgay, long luotSachMuon, long sachQuaHan,
                             long sachMat, long sachHuHong, BigDecimal tongPhiPhat) {
}

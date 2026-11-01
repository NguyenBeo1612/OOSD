package com.eshop.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/** UC01, UC02 – Xem sản phẩm theo nhóm và xem chi tiết. */
@Service
public class SanPhamService {

    private final SanPhamGateway gateway;

    public SanPhamService(SanPhamGateway gateway) {
        this.gateway = gateway;
    }

    public List<Map<String, Object>> layNhom() {
        return gateway.danhSachNhom();
    }

    public List<Map<String, Object>> layTheoNhom(String maNhom) {
        return gateway.danhSachSanPham(maNhom);
    }

    public Map<String, Object> chiTiet(String maSP) {
        Map<String, Object> sp = gateway.thongTinSanPham(maSP);
        if (sp == null) {
            throw new NghiepVuException("Không tìm thấy sản phẩm " + maSP + ".");
        }
        return sp;
    }

    public List<String> hinhAnh(String maSP) {
        return gateway.hinhAnh(maSP);
    }

    public static boolean conHang(Map<String, Object> sp) {
        return TrangThai.CON_HANG.equals(sp.get("TinhTrang"));
    }
}

package com.eshop.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/** UC03, UC04 – Thêm vào giỏ, xem / cập nhật giỏ hàng. */
@Service
public class GioHangService {

    private final SanPhamGateway gateway;

    public GioHangService(SanPhamGateway gateway) {
        this.gateway = gateway;
    }

    public String them(GioHang gio, String maSP, Integer soLuong) {
        int sl = soLuong == null ? 1 : soLuong;
        if (sl <= 0) {
            throw new NghiepVuException("Số lượng phải lớn hơn 0.");
        }
        Map<String, Object> sp = gateway.thongTinSanPham(maSP);
        if (sp == null) {
            throw new NghiepVuException("Không tìm thấy sản phẩm.");
        }
        if (!SanPhamService.conHang(sp)) {
            throw new NghiepVuException("Sản phẩm \"" + sp.get("TenSP") + "\" đã hết hàng.");
        }
        List<String> hinh = gateway.hinhAnh(maSP);
        gio.them(new MucGioHang(maSP, (String) sp.get("TenSP"), hinh.isEmpty() ? null : hinh.get(0), (BigDecimal) sp.get("GiaBan"), sl));
        return "Đã thêm \"" + sp.get("TenSP") + "\" vào giỏ hàng.";
    }

    public String capNhat(GioHang gio, String maSP, Integer soLuong) {
        int sl = soLuong == null ? 0 : soLuong;
        if (!gio.capNhat(maSP, sl)) {
            throw new NghiepVuException("Sản phẩm không có trong giỏ.");
        }
        return sl <= 0 ? "Đã loại sản phẩm khỏi giỏ." : "Đã cập nhật số lượng.";
    }

    public String xoa(GioHang gio, String maSP) {
        gio.xoa(maSP);
        return "Đã loại sản phẩm khỏi giỏ.";
    }

    /** Làm mới đơn giá theo giá hiện hành (giá bán có thể thay đổi trong lúc khách đang chọn). */
    public void capNhatGia(GioHang gio) {
        for (MucGioHang m : gio.getDanhSach()) {
            Map<String, Object> sp = gateway.thongTinSanPham(m.getMaSP());
            if (sp != null) {
                m.setDonGia((BigDecimal) sp.get("GiaBan"));
            }
        }
    }
}

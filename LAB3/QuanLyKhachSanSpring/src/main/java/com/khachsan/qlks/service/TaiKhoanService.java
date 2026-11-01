package com.khachsan.qlks.service;

import static com.khachsan.qlks.service.Db.p;

import java.util.List;
import java.util.Map;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** UC13 – Đăng nhập: vai trò LE_TAN, PHUC_VU, THANH_TOAN, QUAN_LY (actor kế thừa Nhân viên). */
@Service
public class TaiKhoanService implements UserDetailsService {

    private final Db db;

    public TaiKhoanService(Db db) {
        this.db = db;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<Map<String, Object>> l = db.query("SELECT TenDangNhap, MatKhau, VaiTro, HoatDong FROM TaiKhoan WHERE TenDangNhap = :u",
                p("u", username));
        if (l.isEmpty()) {
            throw new UsernameNotFoundException("Không tìm thấy tài khoản " + username);
        }
        Map<String, Object> r = l.get(0);
        return User.withUsername((String) r.get("TenDangNhap"))
                .password((String) r.get("MatKhau"))
                .roles((String) r.get("VaiTro"))
                .disabled(!Boolean.TRUE.equals(r.get("HoatDong")))
                .build();
    }

    /** Nhân viên gắn với tài khoản đang đăng nhập (MaNV, HoTen, VaiTro). */
    public Map<String, Object> nhanVienCua(String username) {
        List<Map<String, Object>> l = db.query("SELECT n.MaNV, n.HoTen, n.VaiTro FROM TaiKhoan t JOIN NhanVien n ON t.MaNV = n.MaNV "
                + "WHERE t.TenDangNhap = :u", p("u", username));
        if (l.isEmpty()) {
            throw new NghiepVuException("Tài khoản chưa gắn với nhân viên.");
        }
        return l.get(0);
    }

    public String maNV(String username) {
        return (String) nhanVienCua(username).get("MaNV");
    }
}

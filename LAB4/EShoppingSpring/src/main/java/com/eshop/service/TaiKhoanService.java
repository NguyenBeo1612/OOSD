package com.eshop.service;

import static com.eshop.service.Db.p;

import java.util.List;
import java.util.Map;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** UC06 – Đăng nhập: khách hàng (ROLE_KHACH) hoặc nhân viên cửa hàng (ROLE_NHANVIEN). */
@Service
public class TaiKhoanService implements UserDetailsService {

    private final Db db;

    public TaiKhoanService(Db db) {
        this.db = db;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<Map<String, Object>> kh = db.query("SELECT TenDangNhap, MatKhau FROM KhachHang WHERE TenDangNhap = :u", p("u", username));
        if (!kh.isEmpty()) {
            return User.withUsername(username).password((String) kh.get(0).get("MatKhau")).roles("KHACH").build();
        }
        List<Map<String, Object>> nv = db.query("SELECT TenDangNhap, MatKhau FROM NhanVien WHERE TenDangNhap = :u", p("u", username));
        if (!nv.isEmpty()) {
            return User.withUsername(username).password((String) nv.get(0).get("MatKhau")).roles("NHANVIEN").build();
        }
        throw new UsernameNotFoundException("Không tìm thấy tài khoản " + username);
    }
}

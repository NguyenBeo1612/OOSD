package com.thuvien.qltv.service;

import com.thuvien.qltv.domain.NhanVien;
import com.thuvien.qltv.domain.TaiKhoan;
import com.thuvien.qltv.repository.TaiKhoanRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** UC_QLTV_001 – Đăng nhập: nạp tài khoản cho Spring Security, vai trò THU_THU / QL_SACH. */
@Service
@Transactional(readOnly = true)
public class TaiKhoanService implements UserDetailsService {

    private final TaiKhoanRepository taiKhoanRepo;

    public TaiKhoanService(TaiKhoanRepository taiKhoanRepo) {
        this.taiKhoanRepo = taiKhoanRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TaiKhoan tk = taiKhoanRepo.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản " + username));
        return User.withUsername(tk.getTenDangNhap())
                .password(tk.getMatKhau())
                .roles(tk.getVaiTro())
                .disabled(!Boolean.TRUE.equals(tk.getHoatDong()))
                .build();
    }

    /** Nhân viên gắn với tài khoản đang đăng nhập – dùng làm "nhân viên lập phiếu". */
    public NhanVien nhanVienCua(String username) {
        return taiKhoanRepo.findById(username)
                .map(TaiKhoan::getNhanVien)
                .orElseThrow(() -> new NghiepVuException("Tài khoản chưa gắn với nhân viên."));
    }
}

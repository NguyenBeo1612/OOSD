package com.eshop.service;

import static com.eshop.service.Db.p;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** UC07 – Đăng ký tài khoản khách hàng. */
@Service
@Transactional
public class KhachHangService {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern DIEN_THOAI = Pattern.compile("^0\\d{9,10}$");

    private final Db db;
    private final PasswordEncoder encoder;
    private final MaSinh maSinh;

    public KhachHangService(Db db, PasswordEncoder encoder, MaSinh maSinh) {
        this.db = db;
        this.encoder = encoder;
        this.maSinh = maSinh;
    }

    public String dangKy(String hoTen, LocalDate ngaySinh, String soCMND, String diaChi, String dienThoai,
                         String tenDangNhap, String matKhau, String email) {
        KiemTra.batBuoc("Vui lòng nhập đủ họ tên, CMND/Passport, địa chỉ, điện thoại, tên đăng nhập và mật khẩu.",
                hoTen, soCMND, diaChi, dienThoai, tenDangNhap, matKhau);
        if (ngaySinh == null || !ngaySinh.isBefore(LocalDate.now())) {
            throw new NghiepVuException("Ngày sinh không hợp lệ.");
        }
        if (!DIEN_THOAI.matcher(dienThoai.trim()).matches()) {
            throw new NghiepVuException("Số điện thoại phải gồm 10–11 chữ số, bắt đầu bằng 0.");
        }
        if (!tenDangNhap.trim().matches("^[A-Za-z0-9_.]{4,50}$")) {
            throw new NghiepVuException("Tên đăng nhập 4–50 ký tự, chỉ gồm chữ, số, dấu chấm, gạch dưới.");
        }
        if (matKhau.length() < 6) {
            throw new NghiepVuException("Mật khẩu phải có ít nhất 6 ký tự.");
        }
        String mail = KiemTra.rong(email) ? null : email.trim();
        if (mail != null && !EMAIL.matcher(mail).matches()) {
            throw new NghiepVuException("Email không đúng định dạng.");
        }
        String ten = tenDangNhap.trim();
        if (db.count("SELECT COUNT(*) FROM KhachHang WHERE TenDangNhap = :u", p("u", ten)) > 0
                || db.count("SELECT COUNT(*) FROM NhanVien WHERE TenDangNhap = :u", p("u", ten)) > 0) {
            throw new NghiepVuException("Tên đăng nhập đã được sử dụng.");
        }
        if (db.count("SELECT COUNT(*) FROM KhachHang WHERE SoCMND = :c", p("c", soCMND.trim())) > 0) {
            throw new NghiepVuException("Số CMND/Passport đã được đăng ký.");
        }
        String maKH = "KH" + maSinh.moi("").substring(6);
        db.execute("INSERT INTO KhachHang(MaKH, HoTen, NgaySinh, SoCMND, DiaChi, DienThoai, TenDangNhap, MatKhau, Email) "
                        + "VALUES(:m, :t, :ns, :c, :dc, :dt, :u, :pw, :e)",
                p("m", maKH, "t", hoTen.trim(), "ns", ngaySinh, "c", soCMND.trim(), "dc", diaChi.trim(), "dt", dienThoai.trim(),
                        "u", ten, "pw", encoder.encode(matKhau), "e", mail));
        return "Đăng ký thành công. Vui lòng đăng nhập bằng tài khoản " + ten + ".";
    }

    @Transactional(readOnly = true)
    public Map<String, Object> theoTenDangNhap(String tenDangNhap) {
        List<Map<String, Object>> l = db.query("SELECT * FROM KhachHang WHERE TenDangNhap = :u", p("u", tenDangNhap));
        if (l.isEmpty()) {
            throw new NghiepVuException("Không tìm thấy khách hàng.");
        }
        return l.get(0);
    }
}

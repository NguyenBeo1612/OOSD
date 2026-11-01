package com.thuvien.qltv.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Phân quyền theo Use Case Diagram:
 * Thủ thư – nhân viên, độc giả & thẻ, mượn, trả; NV quản lý sách – đầu sách, thể loại, NXB;
 * cả hai – thống kê.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/login", "/error").permitAll()
                        .requestMatchers("/sach/**", "/the-loai/**", "/nha-xuat-ban/**").hasRole("QL_SACH")
                        .requestMatchers("/nhan-vien/**", "/doc-gia/**", "/muon/**", "/tra/**").hasRole("THU_THU")
                        .requestMatchers("/thong-ke/**").hasAnyRole("THU_THU", "QL_SACH")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());
        return http.build();
    }

    /** Hỗ trợ chuỗi mật khẩu dạng {bcrypt}$2a$... lưu trong bảng TaiKhoan. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /** Đồng hồ hệ thống – tách ra Bean để kiểm thử được các quy định theo ngày. */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}

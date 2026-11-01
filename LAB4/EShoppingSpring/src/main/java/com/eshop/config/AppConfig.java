package com.eshop.config;

import com.eshop.service.GioHang;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Khách vãng lai xem sản phẩm, dùng giỏ hàng, đăng ký; chỉ khi tính tiền mới yêu cầu đăng nhập
 * (Spring Security tự quay lại trang tính tiền sau khi đăng nhập). Nhân viên quản lý đơn hàng.
 */
@Configuration
public class AppConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/dat-hang/**", "/don-hang/**").hasRole("KHACH")
                        .requestMatchers("/quan-tri/**").hasRole("NHANVIEN")
                        .anyRequest().permitAll())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", false)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    /** Mỗi phiên trình duyệt có một giỏ hàng riêng. */
    @Bean
    @SessionScope
    public GioHang gioHang() {
        return new GioHang();
    }
}

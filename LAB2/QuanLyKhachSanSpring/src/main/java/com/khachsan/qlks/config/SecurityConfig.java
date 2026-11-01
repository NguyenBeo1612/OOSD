package com.khachsan.qlks.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Phân quyền theo Use Case Diagram: Nhân viên (mọi vai trò) – danh mục, dịch vụ;
 * Lễ tân – đặt/nhận phòng; Phục vụ phòng – phòng, tiện nghi, đền bù;
 * Thanh toán – hóa đơn, thanh toán, trả phòng; Quản lý – thống kê và toàn bộ chức năng.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/login", "/error").permitAll()
                        .requestMatchers("/phong/**").hasAnyRole("PHUC_VU", "QUAN_LY")
                        .requestMatchers("/dat-phong/**").hasAnyRole("LE_TAN", "QUAN_LY")
                        .requestMatchers("/tra-phong/den-bu").hasAnyRole("PHUC_VU", "QUAN_LY")
                        .requestMatchers("/tra-phong/hoa-don", "/tra-phong/thanh-toan", "/tra-phong/tra").hasAnyRole("THANH_TOAN", "QUAN_LY")
                        .requestMatchers("/tra-phong/**").hasAnyRole("PHUC_VU", "THANH_TOAN", "QUAN_LY")
                        .requestMatchers("/thong-ke/**").hasRole("QUAN_LY")
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}

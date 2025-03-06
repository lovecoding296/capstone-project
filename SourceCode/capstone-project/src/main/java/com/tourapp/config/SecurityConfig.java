package com.tourapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;


@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Allow all requests
            )
            .csrf().disable() // Disable CSRF (optional)
            .formLogin().disable() // Disable login form
            .httpBasic().disable(); // Disable basic authentication
        return http.build();
    }
    
    
    
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers("/tourist/**").hasRole("TOURIST") // Chỉ Tourist có quyền truy cập
//                .requestMatchers("/tourguide/**").hasRole("TOURGUIDE") // Chỉ TourGuide có quyền truy cập
//                .anyRequest().permitAll() // Các request khác không yêu cầu đăng nhập
//            )
//            .formLogin() // Sử dụng form login mặc định của Spring Security
//            .and()
//            .logout().logoutSuccessUrl("/"); // Đăng xuất xong thì quay về trang chủ
//
//        return http.build();
//    }
}


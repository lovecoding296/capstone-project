package com.tourapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.tourapp.service.CustomUserDetailsService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;


@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	System.out.println("Phi check login");
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Allow all requests
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/", true)
                
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1) // Chỉ cho phép 1 session trên mỗi user
                .maxSessionsPreventsLogin(false) // Nếu user đăng nhập lại thì session cũ bị xóa
            )
            .csrf().disable() // Disable CSRF (optional)
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
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    

}


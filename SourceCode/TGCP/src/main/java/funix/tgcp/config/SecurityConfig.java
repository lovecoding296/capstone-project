package funix.tgcp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;


@Configuration
public class SecurityConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
	
	@Autowired
	private CustomAuthenticationSuccessHandler successHandler;
	
	@Autowired
	private CustomAuthenticationFailureHandler failureHandler;
	
	//@Autowired
	//private JwtFilter authenticationFilter;
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	logger.info("check login");
        http
            .authorizeHttpRequests(auth -> auth
            	.requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/users/bookings/**").authenticated()
            	.requestMatchers("/guides/bookings/**").authenticated()
            	.requestMatchers("/dashboard/**").authenticated()
            	.requestMatchers("/api/notifications/**").authenticated()
            	.requestMatchers("/api/chat/**").authenticated()
            	.requestMatchers("/api/bookings/**").authenticated()
            	.requestMatchers("/api/comments/**").authenticated()
            	
        		.requestMatchers("/ckeditor/**", "/css/**", "/js/**", "/images/**").permitAll()
                .anyRequest().permitAll() // Allow all requests
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(successHandler)
                .failureHandler(failureHandler)
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
            //.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic().disable();            
        return http.build();
    }
    
    
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    
//    @Bean
//    public CorsFilter corsFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.addAllowedOrigin("http://localhost:8080"); // Thay bằng domain frontend
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//        
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }



    

}

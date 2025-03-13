package funix.tca.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
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
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	logger.info("check login");
        http
            .authorizeHttpRequests(auth -> auth
            	.requestMatchers("/admin/**").hasRole("ADMIN")
        		.requestMatchers("/reviews/**").authenticated()
        		.requestMatchers("/appusers/**").authenticated()
        		.requestMatchers("/trips/**").authenticated()
        		.requestMatchers("/posts/**").authenticated()
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
            .httpBasic().disable(); // Disable basic authentication
        return http.build();
    }
    
    
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    

}

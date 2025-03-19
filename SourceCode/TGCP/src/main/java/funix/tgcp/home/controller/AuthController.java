package funix.tgcp.home.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.config.CustomUserDetailsService;
import funix.tgcp.config.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password, HttpServletResponse response) {
        // Xác thực username và password
    	
    	System.out.println("Xác thực username và password " + email + " " + password);
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password));

        // Lấy UserDetails từ database
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

        // Tạo JWT token
        String token = jwtUtil.generateToken(userDetails, userDetails.getId());
        
        ResponseCookie jwtCookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)  // Chỉ dùng với HTTPS
                .path("/")
                .maxAge(Duration.ofHours(1))  // Token có thời hạn 1 giờ
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());


        // Trả về token + userId
        return ResponseEntity.ok(Map.of(
            //"token", token,
            "userId", userDetails.getId()
        ));
    }
}


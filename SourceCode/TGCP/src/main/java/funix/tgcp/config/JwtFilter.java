package funix.tgcp.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        System.out.println("doFilterInternal...");

        String token = getTokenFromHeader(request); // Ưu tiên lấy từ Header
        if (token == null) {
            token = getTokenFromCookies(request); // Nếu không có, lấy từ Cookie
        }

        // Kiểm tra nếu token null hoặc rỗng thì bỏ qua xác thực
        if (token == null || token.trim().isEmpty()) {
            System.out.println("Token is null or empty, skipping authentication.");
            chain.doFilter(request, response);
            return;
        }

        String username = null;
        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            System.out.println("Error extracting username from token: " + e.getMessage());
        }

        // Kiểm tra token hợp lệ và chưa được xác thực
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails auth = authService.loadUserByUsername(username);
            System.out.println("Checking email, password...");

            if (jwtUtil.validateToken(token, auth)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(auth, null, auth.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } else {
            System.out.println("Invalid or missing token.");
        }

        chain.doFilter(request, response);
    }

    // Lấy token từ Header (Authorization: Bearer <token>)
    private String getTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    // Lấy token từ Cookie (HttpOnly)
    private String getTokenFromCookies(HttpServletRequest request) {
    	System.out.println("getTokenFromCookies");
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    System.out.println("JWT token Cookie found: " + cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}

package funix.tgcp.config;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}") // Lấy key từ application.properties
    private String secret;

    @Value("${jwt.expiration}") // Thời gian hết hạn token
    private long expirationTime;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ✅ Tạo JWT từ UserDetails
    public String generateToken(UserDetails userDetails, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId); // Thêm userId vào JWT

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Giải mã JWT và lấy username
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ✅ Giải mã JWT và lấy userId
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    // ✅ Lấy thông tin bất kỳ từ JWT
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ✅ Giải mã JWT và lấy tất cả claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ✅ Kiểm tra token có hết hạn chưa
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // ✅ Kiểm tra token có hợp lệ không
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    
    // Tạo token luôn valid
    public String createToken(Long id, String email) {
    	Map<String, Object> claims = new HashMap<>();
        claims.put("userId", id); // Thêm userId vào JWT

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}

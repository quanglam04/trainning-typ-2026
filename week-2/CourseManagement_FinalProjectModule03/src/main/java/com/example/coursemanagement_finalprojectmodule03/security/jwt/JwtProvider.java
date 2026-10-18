package com.example.coursemanagement_finalprojectmodule03.security.jwt;

import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtProvider {
    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.expired}")
    private Long EXPIRED_TIME;

    @Value("${jwt.refresh-expired}")
    private Long REFRESH_EXPIRED_TIME;


    //=============================1. TẠO TOKEN =======================================
    //băm key đó ra
    public Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //tạo token
    public String generateToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "ACCESS");

        if (userDetails.getUser() != null && userDetails.getUser().getRole() != null) {
            claims.put("role", userDetails.getUser().getRole().name());
        }
        return createToken(claims, userDetails.getUsername(), EXPIRED_TIME);
    }
    public String generateRefreshToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "REFRESH");
        return createToken(claims, userDetails.getUsername(), REFRESH_EXPIRED_TIME);
    }

    private String createToken(Map<String, Object> claims, String username,Long expirationTime) {
        return Jwts.builder()
                .setClaims(claims)  //payload
                .setSubject(username) //payload (chủ thể)
                //thoi gian bat dau
                .setIssuedAt(new Date(System.currentTimeMillis()))
                //thoi gian het han
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                //secret key signature
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    //=============================2. GIẢI MÃ TOKEN =======================================
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //=============================3.XÁC THỰC TOKEN (VALIDATE) =======================================

    //kiểm tra ngày hết hạn (logic: trước ngày hiện tại)
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    //kiểm tra validate đầy đủ
    //token thô: validate 1
    public boolean validateToken(String token) {
        Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token);

        return true;
    }
    //Check token + Đối chiếu với UserDetails
    public boolean validateToken(String token, CustomUserDetails userDetails) {
        if (!validateToken(token)) {
            return false;
        }
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername());
    }
    public boolean isAccessToken(String token) {
        String type = extractClaim(token, claims -> claims.get("type", String.class));
        return "ACCESS".equals(type);
    }
}

package com.sc7258.realworldjava.security;

import com.sc7258.realworldjava.users.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {

    // TODO: Move secret and expiration to configuration file
    // 변경됨: 0.12.x 권장 키 생성 방식 (Jwts.SIG 사용)
    private final SecretKey key = Jwts.SIG.HS256.key().build();
    private final long expirationMs = 86400000; // 24 hours

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())      // 변경됨: setSubject -> subject
                .issuedAt(new Date())             // 변경됨: setIssuedAt -> issuedAt
                .expiration(new Date(System.currentTimeMillis() + expirationMs)) // 변경됨: setExpiration -> expiration
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // 이미 올바르게 수정하신 부분입니다 (유지)
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}

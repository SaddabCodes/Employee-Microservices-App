package com.sadcodes.auth.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationMs;

    // ✅ Generate token from Authentication
    public String generateToken(Authentication authentication) {
        return generateToken(authentication.getName());
    }

    // ✅ Generate token from username
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey()) // OK (still valid)
                .compact();
    }

    // ✅ Extract username (UPDATED)
    public String getUsernameFromJwt(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())   // ✅ NEW method
                .build()
                .parseSignedClaims(token)      // ✅ NEW method
                .getPayload()
                .getSubject();
    }

    // ✅ Validate token (UPDATED)
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())   // ✅ NEW
                    .build()
                    .parseSignedClaims(token);     // ✅ NEW
            return true;

        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        } catch (SecurityException ex) { // replaces SignatureException
            log.error("JWT signature validation failed: {}", ex.getMessage());
        }
        return false;
    }

    // ✅ Signing key
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ✅ Check expiration (UPDATED)
    public boolean isTokenExpired(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public Date getExpirationDateFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();   // ✅ Correct way
    }
}
package com.ecomarket.server.util;

import com.ecomarket.repository.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);
    private static final String SECRET_KEY = "very-secure-32-bytes-long-secret-key-1234567890"; // In Prod use a 32-byte key (e.g. generate with openssl rand -base64 32)
    private static final long EXPIRATION_TIME = 86400000; // 1 day in ms

    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public static String generateToken(User user) {
        LOGGER.debug("Generating JWT for user: {}", user.getEmail());
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("username", user.getUsername())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public static Claims validateToken(String token) {
        LOGGER.debug("Validating JWT");
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            LOGGER.error("Invalid JWT: {}", e.getMessage());
            throw new RuntimeException("Invalid token", e);
        }
    }
}

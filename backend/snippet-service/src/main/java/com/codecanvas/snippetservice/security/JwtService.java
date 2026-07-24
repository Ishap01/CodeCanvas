package com.codecanvas.snippetservice.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(
            @Value("${jwt.secret}") String secret) {

        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must contain at least 32 characters"
            );
        }

        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractSubject(String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    public UUID extractUserId(String token) {

        Claims claims = extractAllClaims(token);

        /*
         * Pehle custom userId claim check karenge.
         */
        String userId = claims.get(
                "userId",
                String.class
        );

        /*
         * Agar userId claim available nahi hai,
         * subject ko UUID assume karenge.
         */
        if (userId == null || userId.isBlank()) {
            userId = claims.getSubject();
        }

        if (userId == null || userId.isBlank()) {
            throw new JwtException(
                    "User id is missing from token"
            );
        }

        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException exception) {
            throw new JwtException(
                    "Invalid user id inside token",
                    exception
            );
        }
    }

    public boolean isTokenValid(String token) {

        try {
            Claims claims = extractAllClaims(token);

            Date expiration = claims.getExpiration();

            return expiration == null
                    || expiration.after(new Date());

        } catch (JwtException
                | IllegalArgumentException exception) {

            return false;
        }
    }
}
package com.codecanvas.userservice.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.codecanvas.userservice.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String generateToken(User user) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "User is required for token generation"
            );
        }

        if (user.getUserId() == null) {
            throw new IllegalArgumentException(
                    "User id is required for token generation"
            );
        }

        if (user.getEmail() == null
                || user.getEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "User email is required for token generation"
            );
        }

        return Jwts.builder()


                .claim(
                        "userId",
                        user.getUserId().toString()
                )


                .subject(user.getEmail())

                .issuedAt(new Date())

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expiration
                        )
                )

                .signWith(getSigningKey())

                .compact();
    }

    @Override
    public String extractEmail(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    private <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {

        Claims claims =
                extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(
            String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        String email =
                extractEmail(token);

        return email.equals(
                userDetails.getUsername()
        )
                && !isTokenExpired(token);
    }

    private Date extractExpiration(
            String token) {

        return extractClaim(
                token,
                Claims::getExpiration
        );
    }

    private boolean isTokenExpired(
            String token) {

        return extractExpiration(token)
                .before(new Date());
    }
}
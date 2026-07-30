package com.codecanvas.paymentservice.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import io.jsonwebtoken.security.Keys;

@Configuration
public class SecurityConfig {

    private final String jwtSecret;

    public SecurityConfig(
            @Value("${jwt.secret}")
            String jwtSecret) {

        this.jwtSecret = jwtSecret;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/payments/webhooks/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/actuator/health"
                        )
                        .permitAll()

                        .anyRequest()
                        .authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(
                                Customizer.withDefaults()
                        )
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {

        if (jwtSecret == null
                || jwtSecret.isBlank()) {

            throw new IllegalStateException(
                    "JWT secret is not configured"
            );
        }

        byte[] secretBytes =
                jwtSecret.getBytes(
                        StandardCharsets.UTF_8
                );

        if (secretBytes.length < 32) {
            throw new IllegalStateException(
                    "JWT secret must contain at least 32 bytes"
            );
        }

        SecretKey secretKey =
                Keys.hmacShaKeyFor(
                        secretBytes
                );

        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(
                        MacAlgorithm.HS256
                )
                .build();
    }
}
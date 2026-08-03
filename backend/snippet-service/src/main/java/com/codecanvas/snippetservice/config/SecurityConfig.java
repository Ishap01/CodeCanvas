package com.codecanvas.snippetservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.codecanvas.snippetservice.security.JwtAuthenticationEntryPoint;
import com.codecanvas.snippetservice.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {

        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;

        this.jwtAuthenticationEntryPoint =
                jwtAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                /*
                 * JWT-based REST API mein CSRF token use nahi hota.
                 */
                .csrf(csrf -> csrf.disable())

                /*
                 * CORS yahan configure nahi karna.
                 * API Gateway CORS handle karega.
                 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                /*
                 * Invalid ya missing JWT ke liye
                 * custom unauthorized response.
                 */
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                jwtAuthenticationEntryPoint
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        /*
                         * Browser preflight request.
                         */
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        /*
                         * Public snippet listing.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/snippets/public"
                        ).permitAll()

                        /*
                         * GET /api/snippets currently
                         * public active snippets return karta hai.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/snippets"
                        ).permitAll()

                        /*
                         * Single snippet details.
                         *
                         * Token optional hai. Service layer
                         * PUBLIC, PREMIUM aur PRIVATE access
                         * decide karegi.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/snippets/*"
                        ).permitAll()

                        /*
                         * Public comment list.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/snippets/*/comments"
                        ).permitAll()

                        /*
                         * Public comment replies.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/comments/*/replies"
                        ).permitAll()

                        /*
                         * Public engagement counts.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/snippets/*/likes"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/snippets/*/bookmarks"
                        ).permitAll()

                        /*
                         * Health endpoints.
                         */
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/info",
                                "/error"
                        ).permitAll()

                        /*
                         * Baaki sab protected:
                         *
                         * create snippet
                         * update/delete snippet
                         * image upload/delete
                         * my snippets
                         * like/bookmark status
                         * add/update/delete comment
                         * fork
                         */
                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form ->
                        form.disable()
                )

                .httpBasic(httpBasic ->
                        httpBasic.disable()
                )

                /*
                 * JWT filter authentication chain mein
                 * username/password filter se pehle chalega.
                 */
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
package com.codecanvas.snippetservice.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.codecanvas.snippetservice.security.JwtAuthenticationEntryPoint;
import com.codecanvas.snippetservice.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter
            jwtAuthenticationFilter;

    private final JwtAuthenticationEntryPoint
            jwtAuthenticationEntryPoint;

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
                 * REST API JWT use kar rahi hai.
                 * Browser session-based CSRF token use nahi ho raha.
                 */
                .csrf(csrf -> csrf.disable())

                /*
                 * React frontend se requests allow hongi.
                 */
                .cors(cors -> cors.configurationSource(
                        corsConfigurationSource()
                ))

                /*
                 * JWT stateless authentication:
                 * server HTTP session create nahi karega.
                 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                /*
                 * Unauthorized request par custom JSON response.
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
                         * Public snippets ki listing.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/snippets/public"
                        ).permitAll()

                        /*
                         * Individual public snippet service layer
                         * visibility check ke through accessible hai.
                         *
                         * Lekin private snippet owner ko token ki
                         * requirement hogi. Is endpoint ko permitAll
                         * rakhne par optional authentication possible hai.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/snippets/*"
                        ).permitAll()

                        /*
                         * Create, update, delete aur user snippets
                         * ke liye valid JWT required hai.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/snippets/**"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/snippets/**"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/snippets/**"
                        ).authenticated()

                        .requestMatchers(
                                "/api/snippets/user/**"
                        ).authenticated()

                        /*
                         * Baaki endpoints protected.
                         */
                        .anyRequest().authenticated()
                )

                /*
                 * Form login aur basic auth ki zarurat nahi.
                 */
                .formLogin(form ->
                        form.disable()
                )

                .httpBasic(httpBasic ->
                        httpBasic.disable()
                )

                /*
                 * JWT filter username/password filter se pehle chalega.
                 */
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource
    corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173"
                )
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept"
                )
        );

        configuration.setExposedHeaders(
                List.of(
                        "Authorization"
                )
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}
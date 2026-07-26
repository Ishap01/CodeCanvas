package com.codecanvas.aiservice.filter;

import com.codecanvas.aiservice.util.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class UserContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String userId = request.getHeader("X-User-Id");

        try {

            if (userId != null && !userId.isBlank()) {
                UserContext.setUserId(UUID.fromString(userId));
            }

            filterChain.doFilter(request, response);

        } finally {
            UserContext.clear();
        }
    }
}
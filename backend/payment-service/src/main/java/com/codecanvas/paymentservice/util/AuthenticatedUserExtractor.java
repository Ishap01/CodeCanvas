package com.codecanvas.paymentservice.util;

import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.codecanvas.paymentservice.exception.UnauthorizedActionException;

@Component
public class AuthenticatedUserExtractor {

    private static final String USER_ID_CLAIM = "userId";

    public UUID extractUserId(Jwt jwt) {

        if (jwt == null) {
            throw new UnauthorizedActionException(
                    "Authentication token is required"
            );
        }

        Object userIdClaim = jwt.getClaim(USER_ID_CLAIM);

        if (userIdClaim == null) {
            throw new UnauthorizedActionException(
                    "Authenticated user ID is missing from token"
            );
        }

        String userIdValue = userIdClaim.toString();

        if (userIdValue.isBlank()) {
            throw new UnauthorizedActionException(
                    "Authenticated user ID is empty in token"
            );
        }

        try {
            return UUID.fromString(userIdValue);

        } catch (IllegalArgumentException exception) {
            throw new UnauthorizedActionException(
                    "Authenticated user ID is invalid"
            );
        }
    }
}
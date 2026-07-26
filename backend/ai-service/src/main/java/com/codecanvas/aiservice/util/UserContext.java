package com.codecanvas.aiservice.util;

import java.util.UUID;

public class UserContext {

    private static final ThreadLocal<UUID> USER_ID = new ThreadLocal<>();

    public static void setUserId(UUID userId) {
        USER_ID.set(userId);
    }

    public static UUID getUserId() {
        return USER_ID.get();
    }

    public static void clear() {
        USER_ID.remove();
    }
}
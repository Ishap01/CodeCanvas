package com.codecanvas.userservice.kafka.constant;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String USER_REGISTERED =
            "user.registered";

    public static final String USER_UPDATED =
            "user.updated";

    public static final String USER_DELETED =
            "user.deleted";
}
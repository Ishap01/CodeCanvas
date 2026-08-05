package com.codecanvas.notificationservice.kafka.constant;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String USER_REGISTERED = "user.registered";
    public static final String USER_UPDATED = "user.updated";
    public static final String USER_DELETED = "user.deleted";

    public static final String SNIPPET_CREATED = "snippet-created";
    public static final String SNIPPET_UPDATED = "snippet-updated";
    public static final String SNIPPET_DELETED = "snippet-deleted";
}

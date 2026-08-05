package com.codecanvas.searchservice.kafka.constant;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    /*
     * =========================================================
     * USER TOPICS
     * =========================================================
     */
    public static final String USER_REGISTERED =
            "user.registered";

    public static final String USER_UPDATED =
            "user.updated";

    public static final String USER_DELETED =
            "user.deleted";

    /*
     * =========================================================
     * SNIPPET TOPICS
     * =========================================================
     */
    public static final String SNIPPET_CREATED =
            "snippet-created";

    public static final String SNIPPET_UPDATED =
            "snippet-updated";

    public static final String SNIPPET_DELETED =
            "snippet-deleted";
}
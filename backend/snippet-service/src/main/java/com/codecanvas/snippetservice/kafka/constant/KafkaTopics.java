package com.codecanvas.snippetservice.kafka.constant;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String SNIPPET_CREATED = "snippet-created";

    public static final String SNIPPET_UPDATED = "snippet-updated";

    public static final String SNIPPET_DELETED = "snippet-deleted";

    public static final String SNIPPET_LIKED = "snippet-liked";

    public static final String SNIPPET_BOOKMARKED = "snippet-bookmarked";

    public static final String COMMENT_CREATED = "comment-created";

    public static final String REPLY_CREATED = "reply-created";


    public static final String SNIPPET_FORKED = "snippet-forked";
}
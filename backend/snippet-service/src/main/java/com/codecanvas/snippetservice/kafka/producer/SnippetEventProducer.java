package com.codecanvas.snippetservice.kafka.producer;

import com.codecanvas.snippetservice.kafka.constant.KafkaTopics;
import com.codecanvas.snippetservice.kafka.event.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.codecanvas.snippetservice.kafka.event.SnippetUpdatedEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnippetEventProducer {

    /*
     * KafkaTemplate is responsible for
     * publishing events to Kafka topics.
     */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishSnippetCreatedEvent(
            SnippetCreatedEvent event) {

        kafkaTemplate.send(
                "snippet-created",
                event
        );
    }

    public void publishSnippetUpdatedEvent(
            SnippetUpdatedEvent event) {

        kafkaTemplate.send(
                KafkaTopics.SNIPPET_UPDATED,
                event.getSnippetId().toString(),
                event
        );

        log.info(
                "Published SnippetUpdatedEvent : {}",
                event.getSnippetId()
        );
    }

    public void publishSnippetDeletedEvent(
            SnippetDeletedEvent event) {

        kafkaTemplate.send(
                KafkaTopics.SNIPPET_DELETED,
                event.getSnippetId().toString(),
                event
        );

        log.info(
                "Published SnippetDeletedEvent : {}",
                event.getSnippetId()
        );
    }


    public void publishSnippetLikedEvent(
            SnippetLikedEvent event) {

        kafkaTemplate.send(
                KafkaTopics.SNIPPET_LIKED,
                event.getSnippetId().toString(),
                event
        );

        log.info(
                "Published SnippetLikedEvent : {}",
                event.getSnippetId()
        );
    }


    /*
     * Publishes snippet bookmarked
     * event to Kafka.
     */
    public void publishSnippetBookmarkedEvent(
            SnippetBookmarkedEvent event) {

        kafkaTemplate.send(
                KafkaTopics.SNIPPET_BOOKMARKED,
                event.getSnippetId().toString(),
                event
        );

        log.info(
                "Published SnippetBookmarkedEvent : {}",
                event.getSnippetId()
        );
    }


    /*
     * Publishes comment created
     * event to Kafka.
     */
    public void publishCommentCreatedEvent(
            CommentCreatedEvent event) {

        kafkaTemplate.send(
                KafkaTopics.COMMENT_CREATED,
                event.getCommentId().toString(),
                event
        );

        log.info(
                "Published CommentCreatedEvent : {}",
                event.getCommentId()
        );
    }


    /*
     * Publishes reply created
     * event to Kafka.
     */
    public void publishReplyCreatedEvent(
            ReplyCreatedEvent event) {

        kafkaTemplate.send(
                KafkaTopics.REPLY_CREATED,
                event.getReplyId().toString(),
                event
        );

        log.info(
                "Published ReplyCreatedEvent : {}",
                event.getReplyId()
        );
    }


    /*
     * Publishes snippet forked
     * event to Kafka.
     */
    public void publishSnippetForkedEvent(
            SnippetForkedEvent event) {

        kafkaTemplate.send(
                KafkaTopics.SNIPPET_FORKED,
                event.getOriginalSnippetId().toString(),
                event
        );

        log.info(
                "Published SnippetForkedEvent : {}",
                event.getOriginalSnippetId()
        );
    }
}
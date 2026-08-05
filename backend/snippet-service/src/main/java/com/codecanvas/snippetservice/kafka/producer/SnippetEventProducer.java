package com.codecanvas.snippetservice.kafka.producer;

import com.codecanvas.snippetservice.kafka.constant.KafkaTopics;
import com.codecanvas.snippetservice.kafka.event.SnippetCreatedEvent;
import com.codecanvas.snippetservice.kafka.event.SnippetDeletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.codecanvas.snippetservice.kafka.event.SnippetUpdatedEvent;
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
                KafkaTopics.SNIPPET_CREATED,
                event.getSnippetId().toString(),
                event
        );

        log.info(
                "Published SnippetCreatedEvent : {}",
                event.getSnippetId()
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
}
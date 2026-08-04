package com.codecanvas.searchservice.kafka.consumer;

import com.codecanvas.searchservice.kafka.constant.KafkaTopics;
import com.codecanvas.searchservice.kafka.event.SnippetDeletedEvent;
import com.codecanvas.searchservice.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnippetDeletedConsumer {

    private final SearchService searchService;

    @KafkaListener(
            topics = KafkaTopics.SNIPPET_DELETED,
            groupId = "search-service",
            containerFactory = "deletedKafkaListenerContainerFactory"
    )
    public void consumeSnippetDeletedEvent(
            SnippetDeletedEvent event) {

        log.info(
                "Received Snippet Deleted Event : {}",
                event.getSnippetId()
        );

        searchService.deleteSnippet(
                event.getSnippetId()
        );

        log.info(
                "Snippet deleted successfully : {}",
                event.getSnippetId()
        );
    }
}
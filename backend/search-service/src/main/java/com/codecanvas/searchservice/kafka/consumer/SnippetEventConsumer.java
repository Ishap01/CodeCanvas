package com.codecanvas.searchservice.kafka.consumer;

import com.codecanvas.searchservice.dto.request.IndexSnippetRequest;
import com.codecanvas.searchservice.kafka.constant.KafkaTopics;
import com.codecanvas.searchservice.kafka.event.SnippetCreatedEvent;
import com.codecanvas.searchservice.kafka.mapper.SnippetEventMapper;
import com.codecanvas.searchservice.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnippetEventConsumer {

    private final SearchService searchService;

    private final SnippetEventMapper snippetEventMapper;

    @KafkaListener(
            topics = KafkaTopics.SNIPPET_CREATED,
            groupId = "search-service"
    )
    public void consumeSnippetCreatedEvent(
            SnippetCreatedEvent event) {

        log.info(
                "Received Snippet Created Event : {}",
                event.getSnippetId()
        );

        IndexSnippetRequest request =
                snippetEventMapper.toIndexSnippetRequest(
                        event
                );

        searchService.indexSnippet(
                request
        );

        log.info(
                "Snippet indexed successfully : {}",
                event.getSnippetId()
        );
    }
}
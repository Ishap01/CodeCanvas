package com.codecanvas.searchservice.kafka.consumer;

import com.codecanvas.searchservice.dto.request.IndexSnippetRequest;
import com.codecanvas.searchservice.kafka.constant.KafkaTopics;
import com.codecanvas.searchservice.kafka.event.SnippetUpdatedEvent;
import com.codecanvas.searchservice.kafka.mapper.SnippetEventMapper;
import com.codecanvas.searchservice.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.codecanvas.searchservice.kafka.event.SnippetUpdatedEvent;
import jakarta.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnippetUpdatedConsumer {

    private final SearchService searchService;

    private final SnippetEventMapper snippetEventMapper;

    @PostConstruct
    public void init() {
        System.out.println("SnippetUpdatedConsumer Bean Created");
    }

    @KafkaListener(
            topics = KafkaTopics.SNIPPET_UPDATED,
            groupId = "search-service",
            containerFactory = "updatedKafkaListenerContainerFactory"
    )
    public void consumeSnippetUpdatedEvent(
            SnippetUpdatedEvent event) {

        log.info("Received Snippet Updated Event : {}", event.getSnippetId());

        IndexSnippetRequest request =
                snippetEventMapper.toIndexSnippetRequest(event);

        searchService.indexSnippet(request);

        log.info("Snippet updated successfully : {}", event.getSnippetId());
    }
}
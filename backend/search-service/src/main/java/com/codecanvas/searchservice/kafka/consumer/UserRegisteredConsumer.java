package com.codecanvas.searchservice.kafka.consumer;

import com.codecanvas.searchservice.kafka.constant.KafkaTopics;
import com.codecanvas.searchservice.kafka.event.UserRegisteredEvent;
import com.codecanvas.searchservice.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredConsumer {

    private final SearchService searchService;

    @KafkaListener(
            topics = KafkaTopics.USER_REGISTERED,
            groupId = "search-service",
            containerFactory = "userRegisteredKafkaListenerContainerFactory"
    )
    public void consumeUserRegisteredEvent(
            UserRegisteredEvent event) {

        log.info(
                "Received User Registered Event : {}",
                event.getUserId()
        );

        /*
         * =========================================================
         * Existing Logic
         * Index user into Elasticsearch
         * =========================================================
         */
        searchService.indexUser(event);

        log.info(
                "User indexed successfully : {}",
                event.getUserId()
        );
    }
}
package com.codecanvas.searchservice.kafka.consumer;

import com.codecanvas.searchservice.kafka.constant.KafkaTopics;
import com.codecanvas.searchservice.kafka.event.UserUpdatedEvent;
import com.codecanvas.searchservice.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserUpdatedConsumer {

    private final SearchService searchService;

    @KafkaListener(
            topics = KafkaTopics.USER_UPDATED,
            groupId = "search-service",
            containerFactory = "userUpdatedKafkaListenerContainerFactory"
    )
    public void consumeUserUpdatedEvent(
            UserUpdatedEvent event) {

        log.info(
                "Received User Updated Event : {}",
                event.getUserId()
        );

        /*
         * =========================================================
         * USER SEARCH INDEX
         * Update user in Elasticsearch
         * =========================================================
         */
        searchService.updateUser(event);

        log.info(
                "User updated successfully : {}",
                event.getUserId()
        );
    }
}
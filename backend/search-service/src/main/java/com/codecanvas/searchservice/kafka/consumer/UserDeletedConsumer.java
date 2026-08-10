package com.codecanvas.searchservice.kafka.consumer;

import com.codecanvas.searchservice.kafka.constant.KafkaTopics;
import com.codecanvas.searchservice.kafka.event.UserDeletedEvent;
import com.codecanvas.searchservice.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDeletedConsumer {

    private final SearchService searchService;

    @KafkaListener(
            topics = KafkaTopics.USER_DELETED,
            groupId = "search-service",
            containerFactory = "userDeletedKafkaListenerContainerFactory"
    )
    public void consumeUserDeletedEvent(
            UserDeletedEvent event) {

        log.info(
                "Received User Deleted Event : {}",
                event.getUserId()
        );

        /*
         * =========================================================
         * USER SEARCH INDEX
         * Remove user from Elasticsearch
         * =========================================================
         */
        searchService.deleteUser(
                event.getUserId()
        );

        log.info(
                "User deleted successfully : {}",
                event.getUserId()
        );
    }
}
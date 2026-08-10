package com.codecanvas.userservice.kafka.producer;

import com.codecanvas.userservice.kafka.constant.KafkaTopics;
import com.codecanvas.userservice.kafka.event.UserRegisteredEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.codecanvas.userservice.kafka.event.UserUpdatedEvent;
import com.codecanvas.userservice.kafka.event.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

//    public UserEventProducer(
//            KafkaTemplate<String, Object> kafkaTemplate) {
//
//        this.kafkaTemplate = kafkaTemplate;
//    }

    public void publishUserRegisteredEvent(
            UserRegisteredEvent event) {

        kafkaTemplate.send(
                KafkaTopics.USER_REGISTERED,
                event.getUserId().toString(),
                event
        );

        log.info(
                "Published UserRegisteredEvent : {}",
                event.getUserId()
        );
    }

    public void publishUserUpdatedEvent(
            UserUpdatedEvent event) {

        kafkaTemplate.send(
                KafkaTopics.USER_UPDATED,
                event.getUserId().toString(),
                event
        );

        log.info(
                "Published UserUpdatedEvent : {}",
                event.getUserId()
        );
    }

    public void publishUserDeletedEvent(
            UserDeletedEvent event) {

        kafkaTemplate.send(
                KafkaTopics.USER_DELETED,
                event.getUserId().toString(),
                event
        );

        log.info(
                "Published UserDeletedEvent : {}",
                event.getUserId()
        );
    }
}
package com.codecanvas.userservice.producer;

import com.codecanvas.userservice.event.UserRegisteredEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventProducer {

    private final KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate;

    public UserEventProducer(
            KafkaTemplate<String, UserRegisteredEvent> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishUserRegisteredEvent(
            UserRegisteredEvent event) {

        kafkaTemplate.send(
                "user-registration",
                event
        );
    }
}
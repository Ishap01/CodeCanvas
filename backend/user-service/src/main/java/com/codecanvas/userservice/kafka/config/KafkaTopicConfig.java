package com.codecanvas.userservice.kafka.config;

import com.codecanvas.userservice.kafka.constant.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic userRegistrationTopic() {

        return TopicBuilder
                .name(KafkaTopics.USER_REGISTERED)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
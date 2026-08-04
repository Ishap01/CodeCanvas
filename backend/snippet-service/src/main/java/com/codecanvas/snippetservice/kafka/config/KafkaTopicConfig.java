package com.codecanvas.snippetservice.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    /*
     * Creates the Kafka topic automatically
     * if it does not already exist.
     */
    @Bean
    public NewTopic snippetCreatedTopic() {

        return TopicBuilder
                .name("snippet-created")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
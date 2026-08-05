package com.codecanvas.snippetservice.kafka.config;

import com.codecanvas.snippetservice.kafka.constant.KafkaTopics;
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


    @Bean
    public NewTopic snippetLikedTopic() {

        return TopicBuilder
                .name(KafkaTopics.SNIPPET_LIKED)
                .partitions(3)
                .replicas(1)
                .build();
    }


    /*
     * Creates Kafka topic
     * for snippet bookmarked events.
     */

    @Bean
    public NewTopic snippetBookmarkedTopic() {

        return TopicBuilder
                .name(KafkaTopics.SNIPPET_BOOKMARKED)
                .partitions(3)
                .replicas(1)
                .build();
    }



    /*
     * Creates Kafka topic
     * for comment created events.
     */
    @Bean
    public NewTopic commentCreatedTopic() {

        return TopicBuilder
                .name(KafkaTopics.COMMENT_CREATED)
                .partitions(3)
                .replicas(1)
                .build();
    }


    /*
     * Creates Kafka topic
     * for reply created events.
     */
    @Bean
    public NewTopic replyCreatedTopic() {

        return TopicBuilder
                .name(KafkaTopics.REPLY_CREATED)
                .partitions(3)
                .replicas(1)
                .build();
    }


    /*
     * Creates Kafka topic
     * for snippet forked events.
     */
    @Bean
    public NewTopic snippetForkedTopic() {

        return TopicBuilder
                .name(KafkaTopics.SNIPPET_FORKED)
                .partitions(3)
                .replicas(1)
                .build();
    }


}
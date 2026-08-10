package com.codecanvas.paymentservice.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    private final String paymentSuccessTopicName;
    private final String paymentFailedTopicName;

    private final int paymentSuccessPartitions;
    private final int paymentSuccessReplicas;

    private final int paymentFailedPartitions;
    private final int paymentFailedReplicas;

    public KafkaTopicConfig(

            @Value("${app.kafka.topics.payment-success}")
            String paymentSuccessTopicName,

            @Value("${app.kafka.topics.payment-failed}")
            String paymentFailedTopicName,

            @Value("${app.kafka.payment-success.partitions}")
            int paymentSuccessPartitions,

            @Value("${app.kafka.payment-success.replicas}")
            int paymentSuccessReplicas,

            @Value("${app.kafka.payment-failed.partitions}")
            int paymentFailedPartitions,

            @Value("${app.kafka.payment-failed.replicas}")
            int paymentFailedReplicas) {

        this.paymentSuccessTopicName = paymentSuccessTopicName;
        this.paymentFailedTopicName = paymentFailedTopicName;

        this.paymentSuccessPartitions = paymentSuccessPartitions;
        this.paymentSuccessReplicas = paymentSuccessReplicas;

        this.paymentFailedPartitions = paymentFailedPartitions;
        this.paymentFailedReplicas = paymentFailedReplicas;
    }

    /**
     * Topic for successful payment events.
     */
    @Bean
    public NewTopic paymentSuccessTopic() {

        return TopicBuilder
                .name(paymentSuccessTopicName)
                .partitions(paymentSuccessPartitions)
                .replicas(paymentSuccessReplicas)
                .build();
    }

    /**
     * Topic for failed payment events.
     */
    @Bean
    public NewTopic paymentFailedTopic() {

        return TopicBuilder
                .name(paymentFailedTopicName)
                .partitions(paymentFailedPartitions)
                .replicas(paymentFailedReplicas)
                .build();
    }
}
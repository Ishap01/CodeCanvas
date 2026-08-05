package com.codecanvas.userservice.kafka.config;

import com.codecanvas.userservice.kafka.event.PaymentFailedEvent;
import com.codecanvas.userservice.kafka.event.PaymentSuccessEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    private final String bootstrapServers;
    private final String consumerGroupId;

    public KafkaConsumerConfig(
            @Value("${spring.kafka.bootstrap-servers}")
            String bootstrapServers,

            @Value("${spring.kafka.consumer.group-id}")
            String consumerGroupId) {

        this.bootstrapServers = bootstrapServers;
        this.consumerGroupId = consumerGroupId;
    }

    /**
     * Success aur failed consumers ke common Kafka properties.
     */
    private Map<String, Object> createCommonConsumerProperties() {

        Map<String, Object> properties =
                new HashMap<>();

        /*
         * Kafka broker address.
         */
        properties.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        /*
         * User Service consumer group.
         */
        properties.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                consumerGroupId
        );

        /*
         * Kafka record key ko String mein deserialize karega.
         */
        properties.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        /*
         * Kafka record value JSON se Java object mein deserialize hogi.
         */
        properties.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class
        );

        /*
         * Offset automatic commit nahi hoga.
         */
        properties.put(
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                false
        );

        /*
         * Group ke offsets available nahi honge to
         * beginning se records read honge.
         */
        properties.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "earliest"
        );

        /*
         * Payment Service ki Java class type header par
         * depend nahi karenge.
         *
         * User Service apni local event class use karegi.
         */
        properties.put(
                JsonDeserializer.USE_TYPE_INFO_HEADERS,
                false
        );

        /*
         * Sirf User Service Kafka event package trusted hai.
         */
        properties.put(
                JsonDeserializer.TRUSTED_PACKAGES,
                "com.codecanvas.userservice.kafka.event"
        );

        return properties;
    }

    /**
     * PAYMENT_SUCCESS event ke liye consumer factory.
     */
    @Bean
    public ConsumerFactory<String, PaymentSuccessEvent>
    paymentSuccessConsumerFactory() {

        Map<String, Object> properties =
                createCommonConsumerProperties();

        /*
         * Success topic ke JSON ko PaymentSuccessEvent
         * class mein deserialize karega.
         */
        properties.put(
                JsonDeserializer.VALUE_DEFAULT_TYPE,
                PaymentSuccessEvent.class.getName()
        );

        return new DefaultKafkaConsumerFactory<>(
                properties
        );
    }

    /**
     * Existing PAYMENT_SUCCESS listener ki default factory.
     *
     * Bean ka naam intentionally kafkaListenerContainerFactory
     * rakha hai, kyunki existing @KafkaListener isi default
     * factory ko use karta hai.
     */
    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<
            String,
            PaymentSuccessEvent
            > kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<
                String,
                PaymentSuccessEvent
                > factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                paymentSuccessConsumerFactory()
        );

        /*
         * Listener successfully complete hone ke baad
         * har record ka offset commit hoga.
         */
        factory.getContainerProperties()
                .setAckMode(
                        ContainerProperties.AckMode.RECORD
                );

        return factory;
    }

    /**
     * PAYMENT_FAILED event ke liye separate consumer factory.
     */
    @Bean
    public ConsumerFactory<String, PaymentFailedEvent>
    paymentFailedConsumerFactory() {

        Map<String, Object> properties =
                createCommonConsumerProperties();

        /*
         * Failed topic ke JSON ko PaymentFailedEvent
         * class mein deserialize karega.
         */
        properties.put(
                JsonDeserializer.VALUE_DEFAULT_TYPE,
                PaymentFailedEvent.class.getName()
        );

        return new DefaultKafkaConsumerFactory<>(
                properties
        );
    }

    /**
     * PAYMENT_FAILED listener ke liye separate
     * Kafka listener container factory.
     */
    @Bean(name = "paymentFailedKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<
            String,
            PaymentFailedEvent
            > paymentFailedKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<
                String,
                PaymentFailedEvent
                > factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                paymentFailedConsumerFactory()
        );

        /*
         * Failed event successfully process hone ke baad
         * us record ka offset commit hoga.
         */
        factory.getContainerProperties()
                .setAckMode(
                        ContainerProperties.AckMode.RECORD
                );

        return factory;
    }
}
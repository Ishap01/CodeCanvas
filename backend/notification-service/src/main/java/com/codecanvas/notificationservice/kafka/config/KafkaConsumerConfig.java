package com.codecanvas.notificationservice.kafka.config;

import com.codecanvas.notificationservice.kafka.event.SnippetCreatedEvent;
import com.codecanvas.notificationservice.kafka.event.SnippetDeletedEvent;
import com.codecanvas.notificationservice.kafka.event.SnippetUpdatedEvent;
import com.codecanvas.notificationservice.kafka.event.UserDeletedEvent;
import com.codecanvas.notificationservice.kafka.event.UserRegisteredEvent;
import com.codecanvas.notificationservice.kafka.event.UserUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:notification-service}")
    private String groupId;

    private Map<String, Object> baseConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return props;
    }

    private <T> JsonDeserializer<T> createJsonDeserializer(Class<T> targetClass) {
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(targetClass);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return deserializer;
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        FixedBackOff backOff = new FixedBackOff(1000L, 3L);
        DefaultErrorHandler handler = new DefaultErrorHandler((record, exception) -> {
            log.error("Kafka consumer error: Failed to process record on topic {} at offset {}. Cause: {}",
                    record.topic(), record.offset(), exception.getMessage());
        }, backOff);
        return handler;
    }

    // User Registered Consumer Factory & Container Factory
    @Bean
    public ConsumerFactory<String, UserRegisteredEvent> userRegisteredConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                baseConsumerProps(),
                new StringDeserializer(),
                createJsonDeserializer(UserRegisteredEvent.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserRegisteredEvent> userRegisteredKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserRegisteredEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userRegisteredConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    // User Updated Consumer Factory & Container Factory
    @Bean
    public ConsumerFactory<String, UserUpdatedEvent> userUpdatedConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                baseConsumerProps(),
                new StringDeserializer(),
                createJsonDeserializer(UserUpdatedEvent.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserUpdatedEvent> userUpdatedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserUpdatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userUpdatedConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    // User Deleted Consumer Factory & Container Factory
    @Bean
    public ConsumerFactory<String, UserDeletedEvent> userDeletedConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                baseConsumerProps(),
                new StringDeserializer(),
                createJsonDeserializer(UserDeletedEvent.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserDeletedEvent> userDeletedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserDeletedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userDeletedConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    // Snippet Created Consumer Factory & Container Factory
    @Bean
    public ConsumerFactory<String, SnippetCreatedEvent> snippetCreatedConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                baseConsumerProps(),
                new StringDeserializer(),
                createJsonDeserializer(SnippetCreatedEvent.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SnippetCreatedEvent> snippetCreatedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SnippetCreatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(snippetCreatedConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    // Snippet Updated Consumer Factory & Container Factory
    @Bean
    public ConsumerFactory<String, SnippetUpdatedEvent> snippetUpdatedConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                baseConsumerProps(),
                new StringDeserializer(),
                createJsonDeserializer(SnippetUpdatedEvent.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SnippetUpdatedEvent> snippetUpdatedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SnippetUpdatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(snippetUpdatedConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    // Snippet Deleted Consumer Factory & Container Factory
    @Bean
    public ConsumerFactory<String, SnippetDeletedEvent> snippetDeletedConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                baseConsumerProps(),
                new StringDeserializer(),
                createJsonDeserializer(SnippetDeletedEvent.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SnippetDeletedEvent> snippetDeletedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SnippetDeletedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(snippetDeletedConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }
}

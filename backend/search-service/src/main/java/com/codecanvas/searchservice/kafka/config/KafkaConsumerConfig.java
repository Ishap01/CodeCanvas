package com.codecanvas.searchservice.kafka.config;

import com.codecanvas.searchservice.kafka.event.SnippetCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import com.codecanvas.searchservice.kafka.event.SnippetUpdatedEvent;
import com.codecanvas.searchservice.kafka.event.SnippetDeletedEvent;
import com.codecanvas.searchservice.kafka.event.UserRegisteredEvent;
import com.codecanvas.searchservice.kafka.event.UserUpdatedEvent;
import com.codecanvas.searchservice.kafka.event.UserDeletedEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, SnippetCreatedEvent> consumerFactory() {

        JsonDeserializer<SnippetCreatedEvent> deserializer =
                new JsonDeserializer<>(SnippetCreatedEvent.class);

        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        Map<String, Object> config = new HashMap<>();

        config.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        config.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "search-service"
        );

        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class
        );

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConsumerFactory<String, SnippetUpdatedEvent> updatedConsumerFactory() {

        JsonDeserializer<SnippetUpdatedEvent> deserializer =
                new JsonDeserializer<>(SnippetUpdatedEvent.class);

        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        Map<String, Object> config = new HashMap<>();

        config.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        config.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "search-service"
        );

        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class
        );

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConsumerFactory<String, SnippetDeletedEvent> deletedConsumerFactory() {

        JsonDeserializer<SnippetDeletedEvent> deserializer =
                new JsonDeserializer<>(SnippetDeletedEvent.class);

        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        Map<String, Object> config = new HashMap<>();

        config.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        config.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "search-service"
        );

        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class
        );

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer
        );
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SnippetUpdatedEvent>
    updatedKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, SnippetUpdatedEvent>
                factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                updatedConsumerFactory()
        );

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SnippetDeletedEvent>
    deletedKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, SnippetDeletedEvent>
                factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                deletedConsumerFactory()
        );

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SnippetCreatedEvent>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, SnippetCreatedEvent>
                factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                consumerFactory()
        );

        return factory;
    }


    @Bean
    public ConsumerFactory<String, UserRegisteredEvent> userRegisteredConsumerFactory() {

        JsonDeserializer<UserRegisteredEvent> deserializer =
                new JsonDeserializer<>(UserRegisteredEvent.class);

        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        Map<String, Object> config = new HashMap<>();

        config.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        config.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "search-service"
        );

        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class
        );

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserRegisteredEvent>
    userRegisteredKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, UserRegisteredEvent>
                factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                userRegisteredConsumerFactory()
        );

        return factory;
    }



    @Bean
    public ConsumerFactory<String, UserUpdatedEvent> userUpdatedConsumerFactory() {

        JsonDeserializer<UserUpdatedEvent> deserializer =
                new JsonDeserializer<>(UserUpdatedEvent.class);

        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        Map<String, Object> config = new HashMap<>();

        config.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        config.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "search-service"
        );

        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class
        );

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserUpdatedEvent>
    userUpdatedKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, UserUpdatedEvent>
                factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                userUpdatedConsumerFactory()
        );

        return factory;
    }


    @Bean
    public ConsumerFactory<String, UserDeletedEvent> userDeletedConsumerFactory() {

        JsonDeserializer<UserDeletedEvent> deserializer =
                new JsonDeserializer<>(UserDeletedEvent.class);

        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);

        Map<String, Object> config = new HashMap<>();

        config.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        config.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "search-service"
        );

        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class
        );

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserDeletedEvent>
    userDeletedKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, UserDeletedEvent>
                factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                userDeletedConsumerFactory()
        );

        return factory;
    }

}
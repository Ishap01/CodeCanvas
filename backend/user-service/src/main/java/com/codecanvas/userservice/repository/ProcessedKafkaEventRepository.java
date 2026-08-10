package com.codecanvas.userservice.repository;

import com.codecanvas.userservice.entity.ProcessedKafkaEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProcessedKafkaEventRepository
        extends JpaRepository<ProcessedKafkaEvent, UUID> {

    boolean existsByPaymentId(UUID paymentId);
}
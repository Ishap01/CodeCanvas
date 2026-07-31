package com.codecanvas.paymentservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codecanvas.paymentservice.entity.WebhookEvent;
import com.codecanvas.paymentservice.enums.WebhookProcessingStatus;

@Repository
public interface WebhookEventRepository
        extends JpaRepository<WebhookEvent, UUID> {

    boolean existsByRazorpayEventId(
            String razorpayEventId
    );

    Optional<WebhookEvent> findByRazorpayEventId(
            String razorpayEventId
    );

    List<WebhookEvent>
    findByStatusOrderByCreatedAtAsc(
            WebhookProcessingStatus status
    );
}
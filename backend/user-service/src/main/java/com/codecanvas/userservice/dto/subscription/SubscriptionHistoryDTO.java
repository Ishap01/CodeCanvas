package com.codecanvas.userservice.dto.subscription;

import com.codecanvas.userservice.entity.SubscriptionHistory;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionHistoryDTO {
    private Long id;
    private String eventType;
    private String description;
    private LocalDateTime createdAt;

    public static SubscriptionHistoryDTO from(SubscriptionHistory history) {
        if (history == null) return null;
        return SubscriptionHistoryDTO.builder()
                .id(history.getId())
                .eventType(history.getEventType() != null ? history.getEventType().name() : null)
                .description(history.getDescription())
                .createdAt(history.getCreatedAt())
                .build();
    }
}

package com.codecanvas.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "premium_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PremiumPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "plan_id")
    private UUID planId;

    @Column(name = "plan_name", nullable = false, length = 50)
    private String planName;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer duration;

    @Column(nullable = false)
    private BigDecimal price;
}
package com.codecanvas.paymentservice.repository;

import com.codecanvas.paymentservice.entity.PremiumPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PremiumPlanRepository extends JpaRepository<PremiumPlan, UUID> {
}
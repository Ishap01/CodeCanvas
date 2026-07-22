package com.codecanvas.userservice.repository;

import com.codecanvas.userservice.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, UUID> {

    Optional<PasswordResetOtp> findByEmail(String email);

    void deleteByEmail(String email);

}
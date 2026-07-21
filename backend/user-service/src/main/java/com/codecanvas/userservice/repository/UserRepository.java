package com.codecanvas.userservice.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.codecanvas.userservice.entity.User;

public interface UserRepository extends JpaRepository<User, UUID>{

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByMobileNumber(String mobileNumber);

    boolean existsByEmail(String email);
}
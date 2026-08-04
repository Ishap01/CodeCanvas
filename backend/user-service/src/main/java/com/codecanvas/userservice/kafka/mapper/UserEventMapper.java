package com.codecanvas.userservice.kafka.mapper;

import com.codecanvas.userservice.entity.User;
import com.codecanvas.userservice.kafka.event.UserDeletedEvent;
import com.codecanvas.userservice.kafka.event.UserRegisteredEvent;
import com.codecanvas.userservice.kafka.event.UserUpdatedEvent;
import org.springframework.stereotype.Component;

@Component
public class UserEventMapper {

    public UserRegisteredEvent toUserRegisteredEvent(
            User user) {

        if (user == null) {
            return null;
        }

        return UserRegisteredEvent.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImage(user.getProfileImage())
                .build();
    }

    public UserUpdatedEvent toUserUpdatedEvent(
            User user) {

        if (user == null) {
            return null;
        }

        return UserUpdatedEvent.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImage(user.getProfileImage())
                .build();
    }

    public UserDeletedEvent toUserDeletedEvent(
            User user) {

        if (user == null) {
            return null;
        }

        return UserDeletedEvent.builder()
                .userId(user.getUserId())
                .build();
    }
}
package com.codecanvas.snippetservice.service;

import com.codecanvas.snippetservice.entity.Snippet;

import java.util.UUID;

public interface ViewService {

    void recordView(
            Snippet snippet,
            UUID currentUserId
    );
}
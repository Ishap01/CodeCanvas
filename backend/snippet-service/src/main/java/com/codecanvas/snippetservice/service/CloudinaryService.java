package com.codecanvas.snippetservice.service;

import org.springframework.web.multipart.MultipartFile;

import com.codecanvas.snippetservice.dto.response.CloudinaryUploadResponse;

public interface CloudinaryService {

    CloudinaryUploadResponse uploadImage(
            MultipartFile file);

    void deleteImage(
            String publicId);
}
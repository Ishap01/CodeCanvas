package com.codecanvas.snippetservice.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.codecanvas.snippetservice.dto.response.CloudinaryUploadResponse;
import com.codecanvas.snippetservice.service.CloudinaryService;

@Service
public class CloudinaryServiceImpl
        implements CloudinaryService {

    private static final long MAX_FILE_SIZE =
            5 * 1024 * 1024;

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(
            Cloudinary cloudinary) {

        this.cloudinary = cloudinary;
    }

    @Override
    public CloudinaryUploadResponse uploadImage(
            MultipartFile file) {

        validateImage(file);

        try {

            String generatedPublicId =
                    UUID.randomUUID().toString();

            Map<?, ?> uploadResult =
                    cloudinary.uploader().upload(
                            file.getBytes(),
                            ObjectUtils.asMap(
                                    "folder",
                                    "codecanvas/snippets",
                                    "public_id",
                                    generatedPublicId,
                                    "resource_type",
                                    "image"
                            )
                    );

            String imageUrl =
                    uploadResult
                            .get("secure_url")
                            .toString();

            String imagePublicId =
                    uploadResult
                            .get("public_id")
                            .toString();

            return new CloudinaryUploadResponse(
                    imageUrl,
                    imagePublicId
            );

        } catch (IOException exception) {

            throw new RuntimeException(
                    "Failed to upload image to Cloudinary",
                    exception
            );
        }
    }

    @Override
    public void deleteImage(
            String publicId) {

        if (publicId == null ||
                publicId.isBlank()) {

            return;
        }

        try {

            Map<?, ?> deleteResult =
                    cloudinary.uploader().destroy(
                            publicId,
                            ObjectUtils.asMap(
                                    "resource_type",
                                    "image",
                                    "invalidate",
                                    true
                            )
                    );

            String result =
                    deleteResult
                            .get("result")
                            .toString();

            if (!result.equals("ok") &&
                    !result.equals("not found")) {

                throw new RuntimeException(
                        "Cloudinary image deletion failed"
                );
            }

        } catch (IOException exception) {

            throw new RuntimeException(
                    "Failed to delete image from Cloudinary",
                    exception
            );
        }
    }

    private void validateImage(
            MultipartFile file) {

        if (file == null || file.isEmpty()) {

            throw new IllegalArgumentException(
                    "Image file is required"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {

            throw new IllegalArgumentException(
                    "Image size must not exceed 5 MB"
            );
        }

        String contentType =
                file.getContentType();

        if (contentType == null ||
                !contentType.startsWith("image/")) {

            throw new IllegalArgumentException(
                    "Only image files are allowed"
            );
        }
    }
}
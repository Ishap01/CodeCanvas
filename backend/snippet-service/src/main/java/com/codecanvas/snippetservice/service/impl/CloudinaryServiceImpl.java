package com.codecanvas.snippetservice.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.codecanvas.snippetservice.service.CloudinaryService;
import java.io.IOException;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

  private static final String FOLDER = "codecanvas/codeImages";

  private final Cloudinary cloudinary;

  public CloudinaryServiceImpl(Cloudinary cloudinary) {

    this.cloudinary = cloudinary;
  }

  @Override
  public String uploadImage(MultipartFile file) {

    validateImageFile(file);

    try {
      Map<?, ?> uploadResult =
          cloudinary
              .uploader()
              .upload(
                  file.getBytes(), ObjectUtils.asMap("folder", FOLDER, "resource_type", "image"));

      return getSecureUrl(uploadResult);

    } catch (IOException exception) {
      throw new IllegalStateException("Failed to upload image", exception);
    }
  }

  @Override
  public String updateImage(MultipartFile file, String publicId) {

    validateImageFile(file);
    validatePublicId(publicId);

    try {
      Map<?, ?> uploadResult =
          cloudinary
              .uploader()
              .upload(
                  file.getBytes(),
                  ObjectUtils.asMap(
                      "public_id",
                      publicId,
                      "overwrite",
                      true,
                      "invalidate",
                      true,
                      "resource_type",
                      "image"));

      return getSecureUrl(uploadResult);

    } catch (IOException exception) {
      throw new IllegalStateException("Failed to update image", exception);
    }
  }

  @Override
  public void deleteImage(String publicId) {

    validatePublicId(publicId);

    try {
      cloudinary
          .uploader()
          .destroy(publicId, ObjectUtils.asMap("resource_type", "image", "invalidate", true));

    } catch (IOException exception) {
      throw new IllegalStateException("Failed to delete image", exception);
    }
  }

  private void validateImageFile(MultipartFile file) {

    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("Image file is required");
    }

    if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {

      throw new IllegalArgumentException("Only image files are allowed");
    }
  }

  private void validatePublicId(String publicId) {

    if (publicId == null || publicId.isBlank()) {
      throw new IllegalArgumentException("Cloudinary public id is required");
    }
  }

  private String getSecureUrl(Map<?, ?> uploadResult) {

    Object secureUrl = uploadResult.get("secure_url");

    if (secureUrl == null) {
      throw new IllegalStateException("Cloudinary did not return an image URL");
    }

    return secureUrl.toString();
  }
}

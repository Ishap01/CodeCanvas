package com.codecanvas.snippetservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

  String uploadImage(MultipartFile file);

  String updateImage(MultipartFile file, String publicId);

  void deleteImage(String publicId);
}

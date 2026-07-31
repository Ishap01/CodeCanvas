package com.codecanvas.snippetservice.dto.response;

public class CloudinaryUploadResponse {

    private String imageUrl;
    private String imagePublicId;

    public CloudinaryUploadResponse() {
    }

    public CloudinaryUploadResponse(
            String imageUrl,
            String imagePublicId) {

        this.imageUrl = imageUrl;
        this.imagePublicId = imagePublicId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImagePublicId() {
        return imagePublicId;
    }

    public void setImagePublicId(String imagePublicId) {
        this.imagePublicId = imagePublicId;
    }
}
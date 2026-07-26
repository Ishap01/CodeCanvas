package com.codecanvas.userservice.dto.request;


public class UserUpdateRequest {

    private String fullName;
    private String mobileNumber;
    private String username;
    private String bio;

    public UserUpdateRequest() {
    }

    public UserUpdateRequest(
            String fullName,
            String mobileNumber,
            String username, String bio) {

        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.username = username;
        this.bio = bio;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

}

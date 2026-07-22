package com.codecanvas.userservice.dto.response;

import java.util.UUID;

public class UserStatisticsResponse {

    private UUID userId;
    private long totalProjects;
    private long totalSnippets;
    private long totalViews;
    private long totalLikes;
    private long totalFavorites;
    private long followers;
    private long following;

    public UserStatisticsResponse() {
    }

    public UserStatisticsResponse(
            UUID userId,
            long totalProjects,
            long totalSnippets,
            long totalViews,
            long totalLikes,
            long totalFavorites,
            long followers,
            long following) {

        this.userId = userId;
        this.totalProjects = totalProjects;
        this.totalSnippets = totalSnippets;
        this.totalViews = totalViews;
        this.totalLikes = totalLikes;
        this.totalFavorites = totalFavorites;
        this.followers = followers;
        this.following = following;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public long getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(long totalProjects) {
        this.totalProjects = totalProjects;
    }

    public long getTotalSnippets() {
        return totalSnippets;
    }

    public void setTotalSnippets(long totalSnippets) {
        this.totalSnippets = totalSnippets;
    }

    public long getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(long totalViews) {
        this.totalViews = totalViews;
    }

    public long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public long getTotalFavorites() {
        return totalFavorites;
    }

    public void setTotalFavorites(long totalFavorites) {
        this.totalFavorites = totalFavorites;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }
}
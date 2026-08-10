package com.codecanvas.userservice.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_statistics")
public class UserStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "statistics_id")
    private UUID statisticsId;

    @OneToOne
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    @Column(name = "total_projects", nullable = false)
    private long totalProjects = 0;

    @Column(name = "total_snippets", nullable = false)
    private long totalSnippets = 0;

    @Column(name = "total_views", nullable = false)
    private long totalViews = 0;

    @Column(name = "total_likes", nullable = false)
    private long totalLikes = 0;

    @Column(name = "total_favorites", nullable = false)
    private long totalFavorites = 0;

    @Column(nullable = false)
    private long followers = 0;

    @Column(nullable = false)
    private long following = 0;

    public UserStatistics() {
    }

    public UserStatistics(User user) {
        this.user = user;
    }

    public UUID getStatisticsId() {
        return statisticsId;
    }

    public void setStatisticsId(UUID statisticsId) {
        this.statisticsId = statisticsId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
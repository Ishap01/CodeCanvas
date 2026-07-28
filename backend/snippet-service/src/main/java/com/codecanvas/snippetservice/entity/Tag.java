package com.codecanvas.snippetservice.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue
    @Column(name = "tag_id", nullable = false, updatable = false)
    private UUID tagId;

    @Column(name = "tag_name", nullable = false, unique = true, length = 50)
    private String tagName;

    @OneToMany(
            mappedBy = "tag",
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<SnippetTag> snippetTags = new ArrayList<>();

    public Tag() {
    }

    public Tag(
            UUID tagId,
            String tagName,
            List<SnippetTag> snippetTags) {

        this.tagId = tagId;
        this.tagName = tagName;
        this.snippetTags = snippetTags;
    }

    public Tag(String tagName) {
        this.tagName = tagName;
    }


    public UUID getTagId() {
        return tagId;
    }

    public void setTagId(UUID tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public List<SnippetTag> getSnippetTags() {
        return snippetTags;
    }

    public void setSnippetTags(List<SnippetTag> snippetTags) {
        this.snippetTags = snippetTags;
    }
}
package com.codecanvas.snippetservice.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "snippet_tags",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_snippet_tag",
                        columnNames = {
                                "snippet_id",
                                "tag_id"
                        }
                )
        }
)
public class SnippetTag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "snippet_tag_id")
    private UUID snippetTagId;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "snippet_id",
            nullable = false
    )
    private Snippet snippet;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "tag_id",
            nullable = false
    )
    private Tag tag;

    public SnippetTag() {
    }

    public SnippetTag(
            Snippet snippet,
            Tag tag) {

        this.snippet = snippet;
        this.tag = tag;
    }

    public UUID getSnippetTagId() {
        return snippetTagId;
    }

    public void setSnippetTagId(UUID snippetTagId) {
        this.snippetTagId = snippetTagId;
    }

    public Snippet getSnippet() {
        return snippet;
    }

    public void setSnippet(Snippet snippet) {
        this.snippet = snippet;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
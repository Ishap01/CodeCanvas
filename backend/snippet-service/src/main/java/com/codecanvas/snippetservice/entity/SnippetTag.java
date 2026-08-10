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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    public SnippetTag(
            Snippet snippet,
            Tag tag) {

        this.snippet = snippet;
        this.tag = tag;
    }
}
package com.codecanvas.snippetservice.entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "snippet_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SnippetFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "file_id")
    private UUID fileId;

    @Column(name = "filename", nullable = false, length = 255)
    private String filename;

    @Column(name = "code", nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(name = "file_order")
    private Integer fileOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snippet_id", nullable = false)
    private Snippet snippet;
}
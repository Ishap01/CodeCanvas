package com.codecanvas.snippetservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "tag_id")
    private UUID tagId;

    @Column(name = "tag_name", nullable = false, unique = true, length = 50)
    private String tagName;
}
package com.codecanvas.snippetservice.repository;

import com.codecanvas.snippetservice.entity.Bookmark;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {

}

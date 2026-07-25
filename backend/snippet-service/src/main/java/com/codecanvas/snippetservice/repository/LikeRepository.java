package com.codecanvas.snippetservice.repository;

import com.codecanvas.snippetservice.entity.Like;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, UUID> {

}

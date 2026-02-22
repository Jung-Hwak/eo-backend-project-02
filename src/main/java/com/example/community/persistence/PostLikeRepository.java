package com.example.community.persistence;

import com.example.community.domain.post.PostLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLikeEntity, Long> {
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    Optional<PostLikeEntity> findByPostIdAndUserId(Long postId, Long userId);
}
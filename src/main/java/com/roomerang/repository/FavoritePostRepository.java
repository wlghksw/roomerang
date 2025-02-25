package com.roomerang.repository;

import com.roomerang.entity.FavoritePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FavoritePostRepository extends JpaRepository<FavoritePost, Long> {

    Optional<FavoritePost> findByUserIdAndPostIdAndPostTypeAndPostTitle(Long userId, Long postId, String postType, String postTitle);

    List<FavoritePost> findByUserId(Long userId);

    void deleteByUserIdAndPostIdAndPostTypeAndPostTitle(Long userId, Long postId, String postType, String postTitle);

    boolean existsByUserIdAndPostIdAndPostType(Long userId, Long postId, String postType);

    FavoritePost findByUserIdAndPostId(Long userId, Long postId);

    void deleteByUserIdAndPostId(Long userId, Long postId);  // 삭제 메서드 추가

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM favorite_posts WHERE match_post_id = :postId OR share_post_id = :postId", nativeQuery = true)
    void deleteByPostId(@Param("postId") Long postId);


}

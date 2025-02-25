package com.roomerang.repository;

import com.roomerang.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    //최신순 정렬 적용
    Page<Post> findByCategoryOrderByPostDateDesc(String category, Pageable pageable);
    Optional<Post> findById(Long id);
    Page<Post> findByCategoryAndRmBoardTitleContainingOrCategoryAndAuthorNameContainingOrderByPostDateDesc(
            String category1, String title, String category2, String author, Pageable pageable);
}

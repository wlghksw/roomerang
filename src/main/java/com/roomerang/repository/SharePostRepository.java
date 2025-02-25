package com.roomerang.repository;

import com.roomerang.entity.SharePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharePostRepository extends JpaRepository<SharePost, Long> {
    Page<SharePost> findByTxnBoardTitleContaining(String keyword, Pageable pageable);

    Page<SharePost> findAll(Pageable pageable);
}
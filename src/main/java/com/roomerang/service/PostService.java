package com.roomerang.service;

import com.roomerang.entity.FavoritePost;
import com.roomerang.entity.Post;
import com.roomerang.repository.FavoritePostRepository;
import com.roomerang.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final FavoritePostRepository favoritePostRepository;

    @Autowired
    public PostService(PostRepository postRepository, FavoritePostRepository favoritePostRepository) {
        this.postRepository = postRepository;
        this.favoritePostRepository = favoritePostRepository;
    }

    public Page<Post> getPostsByCategory(String category, Pageable pageable) {
        return postRepository.findByCategoryOrderByPostDateDesc(category, pageable);
    }

    @Transactional
    public void savePost(Post post) {
        postRepository.save(post);
    }

    @Transactional
    public void savePost(Post post, List<String> imageUrls) {
        post.setPhotoUrls(imageUrls);
        postRepository.save(post);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deletePost(Long id) {
        favoritePostRepository.deleteByPostId(id);

        postRepository.deleteById(id);
    }

    public Page<Post> searchPostsByCategory(String category, String keyword, Pageable pageable) {
        return postRepository.findByCategoryAndRmBoardTitleContainingOrCategoryAndAuthorNameContainingOrderByPostDateDesc(
                category, keyword, category, keyword, pageable);
    }


}
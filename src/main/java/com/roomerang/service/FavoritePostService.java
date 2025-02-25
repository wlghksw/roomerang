package com.roomerang.service;

import com.roomerang.entity.FavoritePost;
import com.roomerang.entity.Post;
import com.roomerang.entity.SharePost;
import com.roomerang.entity.User;
import com.roomerang.repository.FavoritePostRepository;
import com.roomerang.repository.PostRepository;
import com.roomerang.repository.SharePostRepository;
import com.roomerang.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class FavoritePostService {

    private final FavoritePostRepository favoritePostRepository;
    private final PostRepository postRepository;
    private final SharePostRepository sharePostRepository;

    public FavoritePostService(FavoritePostRepository favoritePostRepository,
                               PostRepository postRepository,
                               SharePostRepository sharePostRepository) {
        this.favoritePostRepository = favoritePostRepository;
        this.postRepository = postRepository;
        this.sharePostRepository = sharePostRepository;
    }

    @Transactional
    public String toggleFavorite(Long userId, Long postId, String postType, String postTitle) {

        Optional<FavoritePost> favorite
                = favoritePostRepository.findByUserIdAndPostIdAndPostTypeAndPostTitle(userId, postId, postType, postTitle);

        if (favorite.isPresent()) {
            favoritePostRepository.deleteByUserIdAndPostIdAndPostTypeAndPostTitle(userId, postId, postType, postTitle);
            return "등록 취소되었습니다.";
        } else {
            FavoritePost newFavorite = new FavoritePost();
            newFavorite.setUserId(userId);
            newFavorite.setPostId(postId);
            newFavorite.setPostType(postType);
            newFavorite.setPostTitle(postTitle);

            // match_post_id 또는 share_post_id 자동 설정
            newFavorite.setPost("MATCH".equals(postType) || "NO_ROOM".equals(postType) ? postRepository.findById(postId).orElse(null) : null);
            newFavorite.setSharePost("SHARE".equals(postType) ? sharePostRepository.findById(postId).orElse(null) : null);

            favoritePostRepository.save(newFavorite);
            return "등록되었습니다.";
        }
    }

    public List<FavoritePost> getFavoritePosts(Long userId) {
        return favoritePostRepository.findByUserId(userId);
    }

    public boolean isFavorite(Long userId, Long postId, String postType){
        return favoritePostRepository.existsByUserIdAndPostIdAndPostType(userId, postId, postType);
    }

    @Transactional
    public boolean deleteFavorite(Long userId, Long postId) {
        FavoritePost favoritePost = favoritePostRepository.findByUserIdAndPostId(userId, postId);
        if (favoritePost != null) {
            favoritePostRepository.deleteByUserIdAndPostId(userId, postId);
            return true;
        }
        return false;
    }

}

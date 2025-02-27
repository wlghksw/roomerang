package com.roomerang.service;

import com.roomerang.entity.FavoritePost;
import com.roomerang.entity.SharePost;
import com.roomerang.repository.FavoritePostRepository;
import com.roomerang.repository.SharePostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SharePostService {

    private final SharePostRepository sharePostRepository;
    private final FavoritePostRepository favoritePostRepository;

    private final String uploadDir = System.getProperty("user.dir") + "/uploads";

    public SharePostService(SharePostRepository sharePostRepository, FavoritePostRepository favoritePostRepository) {
        this.sharePostRepository = sharePostRepository;
        this.favoritePostRepository=favoritePostRepository;
    }

    public Page<SharePost> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postDate"));
        return sharePostRepository.findAll(pageable);
    }

    public Page<SharePost> searchPosts(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return sharePostRepository.findAll(pageable);
        }
        return sharePostRepository.findByTxnBoardTitleContaining(keyword, pageable);
    }

    public SharePost getPostById(Long id) {
        return sharePostRepository.findById(id).orElse(null);
    }

    @Transactional
    public void createPost(SharePost sharePost, List<MultipartFile> photos) throws IOException {
        List<String> photoUrls = new ArrayList<>();

        if (photos != null && !photos.isEmpty()) {
            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            for (MultipartFile photo : photos) {
                if (!photo.isEmpty()) {
                    String fileName = UUID.randomUUID() + "_" + photo.getOriginalFilename();
                    File destinationFile = new File(uploadDir, fileName);
                    photo.transferTo(destinationFile);
                    photoUrls.add("/uploads/" + fileName);
                }
            }
        }

        sharePost.setPhotoUrls(photoUrls);
        sharePost.setPostDate(LocalDateTime.now());
        sharePost.setViewCount(0);
        sharePostRepository.save(sharePost);
    }

    @Transactional
    public void updatePost(Long id, SharePost sharePost, List<MultipartFile> photos, List<String> deletePhotos) throws IOException {
        SharePost existingPost = sharePostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        existingPost.setTxnBoardTitle(sharePost.getTxnBoardTitle());
        existingPost.setTxnBoardContent(sharePost.getTxnBoardContent());
        existingPost.setPrice(sharePost.getPrice());
        existingPost.setLocation(sharePost.getLocation());

        //기존 이미지 중 삭제할 이미지 제거
        List<String> updatedPhotoUrls = new ArrayList<>(existingPost.getPhotoUrls());
        if (deletePhotos != null) {
            for (String deleteUrl : deletePhotos) {
                updatedPhotoUrls.remove(deleteUrl);

                //파일 시스템에서도 삭제
                File fileToDelete = new File(System.getProperty("user.dir") + deleteUrl);
                if (fileToDelete.exists()) {
                    fileToDelete.delete();
                }
            }
        }

        //새로운 이미지 추가
        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile photo : photos) {
                if (!photo.isEmpty()) {
                    String fileName = UUID.randomUUID() + "_" + photo.getOriginalFilename();
                    String uploadDir = System.getProperty("user.dir") + "/uploads/";
                    File file = new File(uploadDir, fileName);
                    photo.transferTo(file);
                    updatedPhotoUrls.add("/uploads/" + fileName);
                }
            }
        }

        existingPost.setPhotoUrls(updatedPhotoUrls);
        sharePostRepository.save(existingPost);
    }

    @Transactional
    public void deletePost(Long id) {
        favoritePostRepository.deleteByPostId(id);

        sharePostRepository.deleteById(id);
    }

    @Transactional
    public void shareSavePost(SharePost sharePost) {
        sharePostRepository.save(sharePost);
    }
}

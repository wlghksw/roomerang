package com.roomerang.contoller;

import com.roomerang.entity.FavoritePost;
import com.roomerang.service.FavoritePostService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@Controller
@RequestMapping("/favorite")
public class FavoritePostController {

    private final FavoritePostService favoritePostService;

    public FavoritePostController(FavoritePostService favoritePostService) {
        this.favoritePostService = favoritePostService;
    }

    // 관심글 등록/취소 API (REST API)
    @PostMapping("/toggle")
    public String toggleFavorite(
            @RequestParam Long userId,
            @RequestParam Long postId,
            @RequestParam String postType,
            @RequestParam String postTitle,
            Model model) {

        String message = favoritePostService.toggleFavorite(userId, postId, postType, postTitle);

        model.addAttribute("message", message);

        return "redirect:/favorite/list"; // 관심글 목록으로 리디렉션
    }

    // 관심글 목록 페이지 반환 (Thymeleaf)
    @GetMapping("/list")
    public String getFavoritePosts(@RequestParam Long userId, Model model) {
        List<FavoritePost> favoritePosts = favoritePostService.getFavoritePosts(userId);
        model.addAttribute("userId", userId);
        model.addAttribute("favoritePosts", favoritePosts);
        return "favoriteList"; // Thymeleaf 템플릿 반환
    }

    //관심글 삭제
    @PostMapping("/delete")
    @ResponseBody
    public String deleteFavorite(@RequestParam Long userId, @RequestParam Long postId) {
        boolean deleted = favoritePostService.deleteFavorite(userId, postId);
        return deleted ? "success" : "error";
    }

}
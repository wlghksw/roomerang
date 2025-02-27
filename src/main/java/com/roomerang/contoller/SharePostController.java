package com.roomerang.contoller;

import com.roomerang.entity.Post;
import com.roomerang.entity.SharePost;
import com.roomerang.entity.User;
import com.roomerang.service.FavoritePostService;
import com.roomerang.service.SharePostService;
import com.roomerang.service.UserService;
import com.roomerang.util.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Log4j2
@Controller
@RequestMapping("/share")
public class SharePostController {

    private final SharePostService sharePostService;
    private final FavoritePostService favoritePostService;

    public SharePostController(SharePostService sharePostService, FavoritePostService favoritePostService) {
        this.sharePostService = sharePostService;
        this.favoritePostService = favoritePostService;
    }


    private User getLoginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("세션 없음! (로그인 안 됨)");
            return null;
        }

        User loginUser = (User) session.getAttribute(SessionConst.LOGIN_USER);
        if (loginUser == null) {
            System.out.println("세션에 저장된 로그인 사용자 없음!");
            return null;
        }

        System.out.println("세션에서 로그인 정보 가져오기 성공! username: " + loginUser.getUsername());
        return loginUser;
    }

    //게시글 목록 조회
    @GetMapping("/list")
    public String getAllPosts(@RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "size", defaultValue = "10") int size,
                              Model model, HttpServletRequest request) {
        Page<SharePost> postPage = sharePostService.getAllPosts(page, size);
        User loginUser = getLoginUser(request);


        model.addAttribute("postType","SHARE");
        model.addAttribute("postPage", postPage);
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("loginUser", loginUser);

        return "share/sharePostList";
    }

    // 특정 게시글 조회
    @GetMapping("/{id}")
    public String getPostById(@PathVariable Long id, Model model, HttpServletRequest request) {
        SharePost post = sharePostService.getPostById(id);
        String postType="SHARE";

        post.setViewCount(post.getViewCount() + 1);
        sharePostService.shareSavePost(post);

        // 로그인한 사용자 확인
        HttpSession session = request.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute(SessionConst.LOGIN_USER) : null;

        boolean isFavorite = false;

        if (loginUser != null) {
            isFavorite=favoritePostService.isFavorite(loginUser.getId(), id, postType);
        }

        log.info(isFavorite);
        model.addAttribute("isFavorite", isFavorite);

        model.addAttribute("post", post);
        model.addAttribute("loginUser", loginUser);
        return "share/sharePostView";
    }

    //게시글 작성 페이지 이동
    @GetMapping("/create")
    public String showCreateForm(Model model, HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        if (loginUser == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("post", new SharePost());
        return "share/sharePostWrite";
    }

    //게시글 등록 (여러 장 사진 업로드 가능)
    @PostMapping("/create")
    public String createPost(@ModelAttribute SharePost sharePost,
                             @RequestParam(value = "photos", required = false) List<MultipartFile> photos,
                             HttpServletRequest request) throws IOException {

        User loginUser = getLoginUser(request);
        if (loginUser == null) {
            return "redirect:/auth/login";
        }

        sharePost.setAuthorName(loginUser.getName());
        sharePost.setUserId(loginUser.getUsername());

        sharePostService.createPost(sharePost, photos);
        return "redirect:/share/list";
    }

    //게시글 수정 페이지 이동
    @GetMapping("/{id}/update")
    public String showUpdateForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        if (loginUser == null) {
            return "redirect:/auth/login";
        }

        SharePost post = sharePostService.getPostById(id);
        if (!post.getUserId().equals(loginUser.getUsername())) {
            return "redirect:/share/list";
        }

        model.addAttribute("post", post);
        return "share/sharePostModify";
    }

    //게시글 수정 (여러 장 사진 업로드 가능)
    @PostMapping("/{id}/update")
    public String updatePost(@PathVariable Long id,
                             @ModelAttribute SharePost sharePost,
                             @RequestParam(value = "photos", required = false) List<MultipartFile> photos,
                             @RequestParam(value = "deletePhotos", required = false) List<String> deletePhotos,
                             HttpServletRequest request) throws IOException {

        User loginUser = getLoginUser(request);
        if (loginUser == null) {
            return "redirect:/auth/login";
        }

        SharePost existingPost = sharePostService.getPostById(id);
        if (!existingPost.getUserId().equals(loginUser.getUsername())) {
            return "redirect:/share/list";
        }

        sharePostService.updatePost(id, sharePost, photos, deletePhotos);

        return "redirect:/share/" + id;
    }


    //게시글 삭제
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id, HttpServletRequest request) {
        User loginUser = getLoginUser(request);
        if (loginUser == null) {
            return "redirect:/auth/login";
        }

        sharePostService.deletePost(id);
        return "redirect:/share/list";
    }

    //게시글 검색
    @GetMapping("/search")
    public String searchPosts(@RequestParam(name = "keyword", required = false) String keyword,
                              @RequestParam(name = "page", defaultValue = "0") int page,
                              @RequestParam(name = "size", defaultValue = "10") int size,
                              Model model, HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page, size);

        Page<SharePost> postPage=sharePostService.searchPosts(keyword, pageable);
        User loginUser=getLoginUser(request);

        model.addAttribute("postPage", postPage);
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("loginUser", loginUser);

        return "share/sharePostList";
    }
}

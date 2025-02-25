package com.roomerang.contoller;

import com.roomerang.dto.UserDTO;
import com.roomerang.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/myInfo")
    public void showMyInfo(Long id, Model model){
        log.info("id = " + id);

        UserDTO userDTO = userService.showUserInfo(id);

        model.addAttribute("dto", userDTO);
    }


    @GetMapping("/remove")
    public String remove(@RequestParam("id") Long id, HttpServletRequest request) {
        log.info("회원 삭제 요청 ID: {}", id);
        userService.remove(id);

        // 세션 무효화 (로그아웃 처리)
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return "redirect:/";
    }



}



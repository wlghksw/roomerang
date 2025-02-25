package com.roomerang.contoller;

import com.roomerang.dto.request.UserFindRequest;
import com.roomerang.dto.request.UserVerifyRequest;
import com.roomerang.dto.response.UserFindResponse;
import com.roomerang.repository.UserRepository;
import com.roomerang.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/check-username")
    public ResponseEntity<Map<String, String>> checkUsername(@RequestParam("username") String username) {
        Map<String, String> response = new HashMap<>();

        if (username == null || username.trim().isEmpty()) {
            response.put("error", "아이디를 입력해 주세요.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        boolean exists = userRepository.existsByUsername(username);
        if (exists) {
            response.put("error", "이미 사용 중인 아이디입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        response.put("message", "사용 가능한 아이디입니다.");
        return ResponseEntity.ok(response);
    }

}

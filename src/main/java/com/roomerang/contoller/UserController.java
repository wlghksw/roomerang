package com.roomerang.contoller;

import com.roomerang.dto.UserDTO;
import com.roomerang.dto.request.PasswordResetRequest;
import com.roomerang.entity.User;
import com.roomerang.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

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


    @GetMapping("/changePassword")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("passwordForm", new PasswordResetRequest());
        return "users/changePassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(@SessionAttribute("loginUser") User loginUser,
                                 RedirectAttributes redirectAttributes,
                                 @Validated @ModelAttribute("passwordForm") PasswordResetRequest request,
                                 BindingResult bindingResult,
                                 Model model,
                                 HttpServletRequest httpServletRequest) {
        //@RequestParam("currentPassword") String currentPassword){

        /*if (bindingResult.hasErrors()) {
            log.info("[change-password] 입력 에러: {}", bindingResult);
            model.addAttribute("hasError", true);
            model.addAttribute("errorMessage", bindingResult.getErrorCount());
            return "/users/changePassword";
        }*/

        Long userNo = loginUser.getId();
        log.info("userNo = " + userNo);
        boolean hasError = false;


        //1️⃣ 현재 비밀번호 확인
        int result = userService.passwordConfirmation(request.getCurrentPassword(), userNo);
        log.info("result ---------------=" + result);
        if (result == 0) {

            model.addAttribute("hasCurrentPasswordError", true);
            model.addAttribute("currentPasswordErrorMessage", "현재 비밀번호가 일치하지 않습니다.");

            hasError = true;
        }
        log.info("bindingResult1 = " + bindingResult);

        // 2️⃣ 새 비밀번호 유효성 검사 (길이, 특수문자 포함 여부 등)
        if (bindingResult.hasFieldErrors("newPassword")) {

            model.addAttribute("hasNewPasswordError", true);
            model.addAttribute("newPasswordErrorMessage", "비밀번호는 8~15자의 영문, 숫자, 특수문자를 포함해야 합니다.");

            hasError = true;
        }
        // 3️⃣ 현재 비밀번호와 새 비밀번호 비교
        if (!bindingResult.hasErrors()) {
            userService.validatePassword1(request.getCurrentPassword(), request.getNewPassword(), bindingResult);
            if (bindingResult.hasErrors()) {

                model.addAttribute("hasSamePasswordError", true);
                model.addAttribute("samePasswordErrorMessage", "현재 비밀번호와 동일한 비밀번호는 사용할 수 없습니다.");

                hasError = true;
            }
        }

        log.info("bindingResult2 = " + bindingResult);

        // 4️⃣ 새 비밀번호와 비밀번호 확인 비교
        userService.validatePassword(request.getNewPassword(), request.getPasswordConfirm(), bindingResult);
        log.info("bindingResult3 = " + bindingResult);
        if (bindingResult.hasErrors()) {

            model.addAttribute("hasPasswordConfirmError", true);
            model.addAttribute("passwordConfirmErrorMessage", "새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");

            hasError = true;
        }

        if (hasError) {
            return "users/changePassword";
        }
        boolean success = userService.resetPassword(loginUser.getUsername(), request.getNewPassword());

        if (!success) {

            model.addAttribute("hasGeneralError", true);
            model.addAttribute("generalErrorMessage", "비밀번호 변경에 실패했습니다.");
            return "users/changePassword"; // 실패 시 에러 메시지 출력
        }

        redirectAttributes.addFlashAttribute("successMessage", "비밀번호가 성공적으로 변경되었습니다.");


        return "redirect:/users/changePassword";

    }

    @PostMapping("/remove")
    public ResponseEntity<Map<String, String>> remove(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        log.info("aaaaaaaaaaaaaaaaaaaa");

        if (!requestBody.containsKey("id")) {
            log.warn("회원 삭제 요청 실패: ID가 없음");
            return ResponseEntity.badRequest().body(Map.of("message", "회원 ID가 필요합니다."));
        }

        Long id;

        try {
            id = Long.valueOf(requestBody.get("id").toString());
        } catch (NumberFormatException e) {
            log.error("회원 삭제 요청 실패: 잘못된 ID 값 - {}", requestBody.get("id"));
            return ResponseEntity.badRequest().body(Map.of("message", "잘못된 회원 ID입니다."));
        }

        log.info("회원 삭제 요청 ID: {}", id);
        boolean removeSuccess = userService.remove(id);

        if(!removeSuccess) {
            return ResponseEntity.badRequest().body(Map.of("message", "회원 탈퇴에 실패했습니다."));
        }

        // JSON 응답 반환
        Map<String, String> response = new HashMap<>();
        response.put("message", "회원탈퇴 완료");
        response.put("redirectUrl", "/"); // 프론트에서 직접 처리

        return ResponseEntity.ok(response);
    }


}



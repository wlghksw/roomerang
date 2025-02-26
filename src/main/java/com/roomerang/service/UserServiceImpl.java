package com.roomerang.service;

import com.roomerang.dto.UserDTO;
import com.roomerang.dto.request.UserCreateRequest;
import com.roomerang.dto.request.UserFindRequest;
import com.roomerang.dto.response.UserFindResponse;
import com.roomerang.entity.User;
import com.roomerang.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static com.roomerang.util.StringHelper.maskUsername;
import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> findByUsername(String username) {

        return userRepository.findByUsername(username);
    }

    @Override
    public void validatePassword(String password, String confirmPassword, BindingResult bindingResult) {
        if (!password.equals(confirmPassword)) {
            bindingResult.rejectValue("passwordConfirm", "passwordConfirmError", "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
    }

    @Override
    public boolean createUser(UserCreateRequest userCreateRequest, BindingResult bindingResult) {
        if (userRepository.existsByUsername(userCreateRequest.getUsername())) {
            bindingResult.rejectValue("username", "duplicate", "이미 사용 중인 아이디입니다.");
            return false;
        }
        User user = new User(
                userCreateRequest.getName(),
                userCreateRequest.getBirthDate(),
                userCreateRequest.getGender(),
                userCreateRequest.getUsername(),
                userCreateRequest.getSecurityQuestion(),
                userCreateRequest.getSecurityAnswer(),
                passwordEncoder.encode(userCreateRequest.getPassword()));
        userRepository.save(user);
        return true;
    }

    @Override
    public User login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(rawPassword, u.getPassword()))
                .orElse(null);

        if (user != null) {
            loginUserSession(user); // ✅ 로그인 성공 시 SecurityContext에 저장
        }

        return user;
    }

    private void loginUserSession(User loginUser) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(loginUser.getUsername())
                .password(loginUser.getPassword()) // 비밀번호는 사용되지 않음
                .roles("USER") // 🔹 필요 시 ROLE 추가 가능
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("✅ 로그인 성공: SecurityContext에 사용자 저장 완료! " + loginUser.getUsername());
    }


    @Override
    public List<UserFindResponse> findMaskedUsersAndSecurityQuestion(UserFindRequest userFindRequest) {
        List<User> foundUsers = userRepository.findAllByNameAndBirthDateAndGender(
                userFindRequest.getName(),
                userFindRequest.getBirthDate(),
                userFindRequest.getGender());

        if (foundUsers.isEmpty()) {
            return emptyList();
        }

        return foundUsers.stream()
                .map(user -> new UserFindResponse(
                        maskUsername(user.getUsername()),
                        user.getId(),
                        user.getSecurityQuestion()))
                .toList();
    }

    @Override
    public boolean verifySecurityAnswerByUsername(String username, String securityAnswer) {
        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null) {
            return false;
        }

        return user.getSecurityAnswer().equals(securityAnswer);
    }

    @Override
    public boolean verifySecurityAnswerById(Long userId, String securityAnswer) {
        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            return false;
        }

        return user.getSecurityAnswer().equals(securityAnswer);
    }

    @Override
    public String revealUsername(Long userId) {
        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            return "";
        }

        return user.getUsername();
    }

    @Override
    public UserFindResponse validateUserForPasswordReset(UserFindRequest request) {
        Optional<User> foundUser = userRepository.findByUsername(request.getUsername());

        // 해당 아이디로 조회되는 유저가 없거나, 이름, 생일, 성별이 일치하지 않으면 null 반환
        if (foundUser.isEmpty()) {
            return null;
        }

        // 사용자가 제출한 정보가 모두 일치하는 경우 true 반환
        User user = foundUser.get();

        boolean isExists = user.getName().equals(request.getName()) &&
                user.getBirthDate().equals(request.getBirthDate()) &&
                user.getGender().equals(request.getGender());

        if(!isExists) {
            return null;
        }

        return new UserFindResponse(
                user.getId(),
                user.getSecurityQuestion());
    }


    @Override
    public boolean resetPassword(String username, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Override
    public UserDTO showUserInfo(Long id) {
        Optional<User> result = userRepository.findById(id);

        return result.isPresent() ? entityToDto(result.get()) : null;
    }

    public void validatePassword1(String currentPassword, String newPassword, BindingResult bindingResult) {
        if (currentPassword.equals(newPassword)) {
            bindingResult.rejectValue("newPassword", "newPasswordError", "현재 비밀번호와 동일합니다.");
        }
    }

    @Override
    @Transactional
    public boolean remove(Long id) {

        if (!userRepository.existsById(id)) {
            log.error("사용자 삭제 실패 - 존재하지 않음. ID: {}", id);
            return false;
        }

        userRepository.deleteById(id);
        log.info("사용자 삭제 성공. ID: {}", id);
        return true;
    }

    @Override
    public int passwordConfirmation(String currentPW, Long userNo) {
        log.info("current ; " + currentPW);
        log.info("userNo ; " + userNo);

        return userRepository.findById(userNo).
                filter(u -> passwordEncoder.matches(currentPW, u.getPassword()))
                .map(u ->1)
                .orElse(0);


    }



}


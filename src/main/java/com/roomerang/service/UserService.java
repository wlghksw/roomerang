package com.roomerang.service;

import com.roomerang.dto.UserDTO;
import com.roomerang.dto.request.UserCreateRequest;
import com.roomerang.dto.request.UserFindRequest;
import com.roomerang.dto.response.UserFindResponse;
import com.roomerang.entity.User;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserService {
    void validatePassword(String password, String confirmPassword, BindingResult bindingResult);

    boolean createUser(UserCreateRequest userCreateRequest, BindingResult bindingResult);

    User login(String username, String rawPassword);

    List<UserFindResponse> findMaskedUsersAndSecurityQuestion(UserFindRequest userFindRequest);

    UserFindResponse validateUserForPasswordReset(UserFindRequest userFindRequest);

    boolean verifySecurityAnswerByUsername(String username, String securityAnswer);

    boolean verifySecurityAnswerById(Long userId, String securityAnswer);

    String revealUsername(Long userId);

    boolean resetPassword(String username, String newPassword);

    Optional<User> findByUsername(String username);

    UserDTO showUserInfo(Long id);

    public boolean remove(Long id);

    default User dtoToEntity(UserDTO dto) {
        User entity = User.builder()
                .id(dto.getId())
                .gender(dto.getGender())
                .name(dto.getName())
                .securityQuestion(dto.getSecurityQuestion())
                .password(dto.getPassword())
                .birthDate(LocalDate.parse(dto.getBirthDate()))
                .securityAnswer(dto.getSecurityAnswer())
                .username(dto.getUsername())
                .build();
        return entity;

    }

    default UserDTO entityToDto(User user) {
        UserDTO dto = UserDTO.builder()
                .id(user.getId())
                .gender(user.getGender())
                .name(user.getName())
                .securityAnswer(user.getSecurityAnswer())
                .securityQuestion(user.getSecurityQuestion())
                .password(user.getPassword())
                .birthDate(String.valueOf(user.getBirthDate()))
                .username(user.getUsername())
                .build();
        return dto;
    }


}
package com.roomerang.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserFindResponse {
    @NotNull
    private String maskedUsername;

    @NotNull
    private Long userId;

    @NotNull
    private String securityQuestion;

    public UserFindResponse(Long userId, String securityQuestion) {
        this.userId = userId;
        this.securityQuestion = securityQuestion;
    }
}

package com.roomerang.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVerifyRequest {
    private long userId;

    private String username;

    @NotNull
    private String securityAnswer;

    public UserVerifyRequest(String username, String securityAnswer) {
        this.username = username;
        this.securityAnswer = securityAnswer;
    }
}

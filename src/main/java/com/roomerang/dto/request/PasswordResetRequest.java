package com.roomerang.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequest {
    @NotBlank
    private String newPassword;

    @NotBlank
    private String passwordConfirm;
}

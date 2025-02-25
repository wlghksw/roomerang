package com.roomerang.dto.request;

import com.roomerang.entity.User;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequest {
    @NotBlank
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    @Past
    private LocalDate birthDate;

    @NotNull
    private User.Gender gender;

    @NotBlank
    private String username;

    @NotBlank
    private String securityQuestion;

    @NotBlank
    private String securityAnswer;

    @NotBlank
    @Size(min = 8, max = 15)
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$"
    )
    private String password;

    @NotBlank
    private String passwordConfirm;
}


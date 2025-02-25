package com.roomerang.dto.request;

import com.roomerang.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFindRequest {
    @NotBlank
    private String name;

    private String username;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    @Past
    private LocalDate birthDate;

    @NotNull
    private User.Gender gender;
}

package com.roomerang.dto;

import com.roomerang.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String name;
    private String birthDate;
    private User.Gender gender;
    private String username;
    private String securityQuestion;
    private String securityAnswer;
    private String password;

}


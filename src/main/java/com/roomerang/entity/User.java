package com.roomerang.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthDate;

    public enum Gender {
        MALE, FEMALE
    };

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "security_question", nullable = false)
    private String securityQuestion;

    @Column(name = "security_answer" , nullable = false)
    private String securityAnswer;
    private String password;

    public User(String name, LocalDate birthDate, Gender gender, String username, String securityQuestion, String securityAnswer, String password) {
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.username = username;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.password = password;
    }
    public int getAge() {
        return Period.between(this.birthDate, LocalDate.now()).getYears();
    }
}
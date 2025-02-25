package com.roomerang.repository;

import com.roomerang.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    List<User> findAllByNameAndBirthDateAndGender(String name, LocalDate birthDate, User.Gender gender);
    @Query("SELECT u.username FROM User u WHERE u.id = :id")
    String findUsernameById(@Param("id") Long id);
    @Query("SELECT u.securityQuestion FROM User u WHERE u.id = :id")
    String findSecurityQuestionByUserId(@Param("id") Long id);
}

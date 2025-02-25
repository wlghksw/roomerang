package com.roomerang.service;

import com.roomerang.dto.request.UserLoginRequest;
import com.roomerang.dto.request.UserCreateRequest;
import com.roomerang.entity.User;
import com.roomerang.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    //Helper Method
    private static UserCreateRequest createUserSignupDto() {
        return new UserCreateRequest(
                "카리나",
                LocalDate.of(2000, 4, 11),
                User.Gender.FEMALE,
                "user01",
                "가장 좋아했던 동화책의 제목은 무엇입니까?",
                "어린왕자",
                "Default@1234",
                "Default@1234"
        );
    }

    private static User createSavedUser() {
        return new User(
                1L,
                "카리나",
                LocalDate.of(2000, 4, 11),
                User.Gender.FEMALE,
                "user01",
                "가장 좋아했던 동화책의 제목은 무엇입니까?",
                "어린왕자",
                "$2b$12$nJckdyviXDWfjAgaCSLKy.u1NrNOc1aul649r6VyMyW1ZlHVXPqXq"
        );
    }

    @Test
    void 비밀번호_확인_불일치() {
        //given: 회원가입 시 입력하는 비밀번호·비밀번호 확인을 불일치하게 설정
        UserCreateRequest dto = createUserSignupDto();
        dto.setPassword("1234");
        dto.setPasswordConfirm("5678");

        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "user");

        //when: 비밀번호 확인 검증 로직 실행
        userService.validatePassword(dto.getPassword(),
                dto.getPasswordConfirm(),
                bindingResult);

        //then: 에러 발생
        assertThat(bindingResult.hasErrors()).isTrue();
    }

    @Test
    void 비밀번호_확인_일치() {
        //given: 회원가입 시 입력하는 비밀번호·비밀번호 확인이 일치하도록 설정
        UserCreateRequest dto = createUserSignupDto();
        dto.setPassword("1234");
        dto.setPasswordConfirm("1234");

        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "user");

        //when: 비밀번호 확인 검증 로직 실행
        userService.validatePassword(dto.getPassword(),
                dto.getPasswordConfirm(),
                bindingResult);

        //then: 에러가 발생하지 않음
        assertThat(bindingResult.hasErrors()).isFalse();
    }

    @Test
    void 회원가입() {
        //given: 유효한 dto를 제출
        UserCreateRequest dto = createUserSignupDto();
        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "user");

        //mocking
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(
                invocation -> {
                    User testUser = invocation.getArgument(0);
                    testUser.setId(1L); // DB에서 생성된 ID(PK)처럼 값을 설정
                    return testUser;
                });

        //when: 회원가입 로직 실행
        userService.createUser(dto, bindingResult);

        //then: userRepository.save(user)가 정상적으로 호출되었는지 검증
        verify(userRepository).save(any(User.class));
        assertThat(bindingResult.hasErrors()).isFalse();

        //평문의 비밀번호가 아닌, encode된 비밀번호로 userRepository.save()가 실행되었는지 확인
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    void 로그인() {
        //given: 올바른 아이디, 비밀번호 쌍을 제출
        User savedUser = createSavedUser();
        UserLoginRequest dto = new UserLoginRequest(savedUser.getUsername(), "rawPassword");

        //mocking
        when(userRepository.findByUsername(any(String.class))).thenAnswer(
                invocation -> {
                    String loginId = invocation.getArgument(0);
                    return loginId.equals(savedUser.getUsername())
                            ? Optional.of(savedUser)
                            : Optional.empty();
                });
        when(passwordEncoder.matches(dto.getPassword(),savedUser.getPassword())).thenReturn(true);

        //when: 로그인 로직 실행
        User loginUser = userService.login(dto.getUsername(), dto.getPassword());

        //then:
        assertThat(loginUser).isEqualTo(savedUser);
    }

    @Test
    void 존재하지_않는_아이디() {
        //given: 존재하지 않는 아이디를 제출
        User savedUser = createSavedUser();
        UserLoginRequest dto = new UserLoginRequest("invalidUsername", "rawPassword");

        //mocking
        when(userRepository.findByUsername(any(String.class))).thenAnswer(
                invocation -> {
                    String loginId = invocation.getArgument(0);
                    return loginId.equals(savedUser.getUsername())
                            ? Optional.of(savedUser)
                            : Optional.empty();
                });

        //when: 로그인 로직 실행
        User loginUser = userService.login(dto.getUsername(), dto.getPassword());

        //then:
        assertThat(loginUser).isNull();
    }

    @Test
    void 잘못된_비밀번호() {
        // TODO
    }

}
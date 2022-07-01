package com.todayhouse.domain.user.application;

import com.todayhouse.domain.email.dao.EmailVerificationTokenRepository;
import com.todayhouse.domain.email.domain.EmailVerificationToken;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.dto.request.PasswordUpdateRequest;
import com.todayhouse.domain.user.dto.request.UserLoginRequest;
import com.todayhouse.domain.user.dto.request.UserSignupRequest;
import com.todayhouse.domain.user.exception.SignupPasswordException;
import com.todayhouse.domain.user.exception.UserNicknameExistException;
import com.todayhouse.domain.user.exception.WrongPasswordException;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import com.todayhouse.infra.S3Storage.service.FileServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    FileServiceImpl fileService;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    EmailVerificationTokenRepository emailVerificationTokenRepository;

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("존재하는 email인지 확인")
    void existByEmail() {
        String email = "test@test.com";
        String anyEmail = "any";
        when(userRepository.existsByEmailAndNicknameIsNotNull(email)).thenReturn(true);
        when(userRepository.existsByEmailAndNicknameIsNotNull(anyEmail)).thenReturn(false);

        assertThat(userService.existByEmail(email)).isTrue();
        assertThat(userService.existByEmail(anyEmail)).isFalse();
    }

    @Test
    @DisplayName("존재하는 nickname인지 확인")
    void existByNickname() {
        String nickname = "test";
        String anyNickname = "any";
        when(userRepository.existsByNickname(nickname)).thenReturn(true);
        when(userRepository.existsByNickname(anyNickname)).thenReturn(false);

        assertThat(userService.existByNickname(nickname)).isTrue();
        assertThat(userService.existByNickname(anyNickname)).isFalse();
    }

    @Test
    @DisplayName("이메일 인증한 유저 저장")
    void saveUser() {
        String email = "test@test.com";
        UserSignupRequest requset = UserSignupRequest.builder()
                .email(email).nickname("test").password1("12345678").password2("12345678")
                .agreePICU(true).agreeAge(true).agreePromotion(true).agreeTOS(true)
                .build();
        User result = User.builder().id(1L).email(email).password("12345").build();
        when(emailVerificationTokenRepository.findByEmailAndExpired(email, true))
                .thenReturn(Optional.of(new EmailVerificationToken()));
        when(userRepository.save(any(User.class))).thenReturn(result);

        assertThat(userService.saveUser(requset)).isEqualTo(result);
    }

    @Test
    @DisplayName("이메일 인증하지 않은 유저 저장")
    void saveUserError() {
        String email = "error@error.com";
        UserSignupRequest requset = UserSignupRequest.builder()
                .email(email).nickname("test").password1("12345678").password2("12345678")
                .agreePICU(true).agreeAge(true).agreePromotion(true).agreeTOS(true)
                .build();
        when(emailVerificationTokenRepository.findByEmailAndExpired(email, true))
                .thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(requset));
    }

    @Test
    @DisplayName("정상 로그인")
    void login() {
        String email = "test@test.com";
        String jwt = "jwt";
        UserLoginRequest request = UserLoginRequest.builder().email(email).password("12345").build();
        User findUser = User.builder().id(1L).email(email).password("12345")
                .roles(Collections.singletonList(Role.USER)).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(findUser));
        when(bCryptPasswordEncoder.matches(request.getPassword(), findUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.createToken(eq(email), anyList())).thenReturn(jwt);

        assertThat(userService.login(request).getId()).isEqualTo(findUser.getId());
        assertThat(userService.login(request).getAccessToken()).isEqualTo(jwt);
    }

    @Test
    @DisplayName("잘못된 로그인 비밀번호")
    void loginError() {
        String email = "test@test.com";
        UserLoginRequest request = UserLoginRequest.builder().email(email).password("12345").build();
        User findUser = User.builder().email(email).password("12345")
                .roles(Collections.singletonList(Role.USER)).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(findUser));
        when(bCryptPasswordEncoder.matches(request.getPassword(), findUser.getPassword())).thenReturn(false);

        assertThrows(WrongPasswordException.class, () -> userService.login(request));
    }

    @Test
    @DisplayName("비밀번호 변경")
    void passwordUpdate() {
        String email = "test@test.com";
        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
                .password1("abcde").password2("abcde")
                .build();
        User user = User.builder().password("123456").build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(user));
        checkEmailInvalidation(email);

        userService.updatePassword(request);
        assertThat(new BCryptPasswordEncoder().matches(request.getPassword1(), user.getPassword())).isTrue();
    }

    @Test
    @DisplayName("비밀번호 확인이 다름")
    void passwordError() {
        String email = "test@test.com";
        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
                .password1("abcde").password2("abcdea")
                .build();
        assertThrows(SignupPasswordException.class, () -> userService.updatePassword(request));
    }

    @Test
    @DisplayName("유저 정보 수정")
    void updateUserInfo() {
        String email = "test@test";
        String uploadImg = "newImg";
        String oldImg = "oldImg";
        String newImgUrl = "newImg.com";
        String newNickname = "newNickname";
        MultipartFile profileImg = mock(MultipartFile.class);
        User oldUser = User.builder()
                .email(email)
                .gender("male")
                .birth("2022-1-1")
                .nickname("oldNickname")
                .profileImage("https://todayhouse/oldImg")
                .introduction("hello world!").build();

        User newUser = User.builder()
                .email(email)
                .gender("female")
                .birth("2022-1-11")
                .nickname(newNickname)
                .introduction("hello world!^^").build();

        checkEmailInvalidation(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(oldUser));
        when(userRepository.existsByNickname(anyString())).thenReturn(false);
        when(fileService.changeUrlToFileName(anyString())).thenReturn(oldImg);
        doNothing().when(fileService).deleteOne(anyString());
        when(fileService.uploadImage(any(MultipartFile.class))).thenReturn(uploadImg);
        when(fileService.changeFileNameToUrl(uploadImg)).thenReturn(newImgUrl);

        userService.updateUserInfo(profileImg, newUser);
        assertThat(oldUser).usingRecursiveComparison().isEqualTo(newUser);
        assertThat(oldUser.getProfileImage()).isEqualTo(newImgUrl);
    }

    @Test
    @DisplayName("다른 중복된 닉네임으로 유저 정보 변경")
    void updateUserInfoNicknameDuplicateException() {
        String email = "test@test";
        User newUser = User.builder()
                .email(email)
                .nickname("duplicate").build();

        when(userRepository.existsByNickname(anyString())).thenReturn(true);

        assertThrows(UserNicknameExistException.class, () ->
                userService.updateUserInfo(null, newUser));
    }

    @Test
    @DisplayName("유저 정보 수정에 MultipartFile은 null로 요청")
    void updateUserInfoFileNull() {
        String email = "test@test";
        User oldUser = User.builder()
                .email(email)
                .gender("m")
                .birth("2022-1-1")
                .nickname("oldNickname")
                .profileImage("oldImg")
                .introduction("hello world!").build();

        User newUser = User.builder()
                .email(email)
                .birth("")
                .gender("")
                .nickname("")
                .introduction("").build();

        checkEmailInvalidation(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(oldUser));
        when(userRepository.existsByNickname(anyString())).thenReturn(false);

        userService.updateUserInfo(null, newUser);
        assertThat(oldUser.getGender()).isEqualTo("m");
        assertThat(oldUser.getProfileImage()).isEqualTo("oldImg");
    }

    private void checkEmailInvalidation(String email) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }
}
package com.todayhouse.domain.user.oauth.application;

import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.dto.response.UserSignupResponse;
import com.todayhouse.domain.user.oauth.dto.request.OAuthSignupRequest;
import com.todayhouse.domain.user.oauth.exception.AuthGuestException;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OAuthServiceImplTest {

    @InjectMocks
    OAuthServiceImpl oAuthService;

    @Mock
    UserRepository userRepository;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("email로 nickname 찾기")
    void findNicknamebyEmail() {
        String email = "test@test.com";
        String fakeEmail = "test@test";

        User user = User.builder().email(email).nickname("test").build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));
        when(userRepository.findByEmail(fakeEmail)).thenThrow(new IllegalArgumentException());

        String nickname = oAuthService.findNicknamebyEmail(email);

        verify(userRepository).findByEmail(email);
        assertThat(nickname.equals("test")).isTrue();

        assertThrows(IllegalArgumentException.class, () -> oAuthService.findNicknamebyEmail(fakeEmail));
    }

    @Test
    @DisplayName("guest email로 jwt 발급 요청")
    void provideTokenError() {
        String email = "user@user.com";
        User findUser = User.builder().id(1L).email(email)
                .roles(Collections.singletonList(Role.GUEST)).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(findUser));

        assertThrows(AuthGuestException.class, () -> oAuthService.provideToken(email));
    }

    @Test
    @DisplayName("user email로 jwt 발급 요청")
    void provideToken() {
        String email = "user@user.com";
        User findUser = User.builder().id(1L).email(email)
                .roles(Collections.singletonList(Role.USER)).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(findUser));
        when(jwtTokenProvider.createToken(findUser.getEmail(), findUser.getRoles()))
                .thenReturn("jwt~~!~!~!~!");

        assertThat(oAuthService.provideToken(email)).isEqualTo("jwt~~!~!~!~!");
    }

    @Test
    @DisplayName("guest 회원가입")
    void saveUser() {
        String email = "test@test.com";
        User guest = User.builder()
                .email(email)
                .authProvider(AuthProvider.NAVER)
                .roles(Collections.singletonList(Role.GUEST))
                .build();
        OAuthSignupRequest request = OAuthSignupRequest.builder().email(email).nickname("test")
                .agreePICU(true).agreeTOS(true).agreePromotion(true).agreeAge(true)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(guest));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(guest, "", null);
        when(jwtTokenProvider.getAuthentication(anyString())).thenReturn(auth);

        UserSignupResponse userSignupResponse = new UserSignupResponse(oAuthService.saveGuest(request, "jwt"));

        assertThat(userSignupResponse.getEmail().equals(email)).isTrue();
        assertThat(userSignupResponse.getNickname().equals("test")).isTrue();
    }
}
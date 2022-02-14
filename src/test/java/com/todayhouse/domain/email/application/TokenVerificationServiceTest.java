package com.todayhouse.domain.email.application;

import com.todayhouse.domain.email.dao.EmailVerificationTokenRepository;
import com.todayhouse.domain.email.domain.EmailVerificationToken;
import com.todayhouse.domain.email.dto.request.TokenVerificationRequest;
import com.todayhouse.global.config.cookie.CookieUtils;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenVerificationServiceTest {

    @InjectMocks
    TokenVerificationService service;

    @Mock
    EmailVerificationTokenRepository repository;

    @Mock
    JwtTokenProvider jwtTokenProvider;


    @Test
    void 인증코드_확인() {
        String token = "123890";
        String email = "test@email.com";
        TokenVerificationRequest request = TokenVerificationRequest.builder()
                .token(token).email(email).build();
        EmailVerificationToken save = EmailVerificationToken.createEmailToken(email, token);
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();

        when(repository.findByEmailAndTokenAndExpiredAtAfterAndExpired(
                eq(request.getEmail()), eq(request.getToken()), any(LocalDateTime.class), eq(false))
        ).thenReturn(Optional.of(save));
        when(jwtTokenProvider.createToken(anyString(), anyList())).thenReturn("jwt");


        EmailVerificationToken result = service.verifyToken(request, servletResponse);

        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getToken()).isEqualTo(token);
        assertThat(result.isExpired()).isEqualTo(true);
        assertThat(result.getExpiredAt()).isAfter(LocalDateTime.now());
        assertThat(result.getExpiredAt()).isBefore(LocalDateTime.now().plusMinutes(3l));
    }

    @Test
    void 유효하지_않은_인증코드() {
        String token = "123890";
        String email = "test@email.com";
        TokenVerificationRequest request = TokenVerificationRequest.builder()
                .token(token).email(email).build();
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();

        when(repository.findByEmailAndTokenAndExpiredAtAfterAndExpired(
                eq(request.getEmail()), eq(request.getToken()), any(LocalDateTime.class), eq(false))
        ).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> service.verifyToken(request, servletResponse));
    }

    @Test
    void 쿠키_테스트() {
        String token = "123890";
        String email = "test@email.com";
        TokenVerificationRequest request = TokenVerificationRequest.builder()
                .token(token).email(email).build();
        EmailVerificationToken save = EmailVerificationToken.createEmailToken(email, token);
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();

        when(repository.findByEmailAndTokenAndExpiredAtAfterAndExpired(
                eq(request.getEmail()), eq(request.getToken()), any(LocalDateTime.class), eq(false))
        ).thenReturn(Optional.of(save));
        when(jwtTokenProvider.createToken(anyString(), anyList())).thenReturn("jwt");

        service.verifyToken(request, servletResponse);

        assertThat(servletResponse.getCookie("auth_user").getValue()).isEqualTo(CookieUtils.serialize("jwt"));
    }
}
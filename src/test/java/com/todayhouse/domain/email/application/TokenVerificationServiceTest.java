package com.todayhouse.domain.email.application;

import com.todayhouse.domain.email.dao.EmailVerificationTokenRepository;
import com.todayhouse.domain.email.domain.EmailVerificationToken;
import com.todayhouse.domain.email.dto.request.TokenVerificationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenVerificationServiceTest {

    @InjectMocks
    TokenVerificationService service;

    @Mock
    EmailVerificationTokenRepository repository;

    @Test
    void 인증코드_확인() {
        String token = "123890";
        String email = "test@email.com";
        TokenVerificationRequest request = TokenVerificationRequest.builder()
                .token(token).email(email).build();
        EmailVerificationToken save = EmailVerificationToken.createEmailToken(email, token);

        when(repository.findByEmailAndTokenAndExpiredAtAfterAndExpired(
                eq(request.getEmail()), eq(request.getToken()), any(LocalDateTime.class), eq(false))
        ).thenReturn(Optional.of(save));


        EmailVerificationToken result = service.verifyToken(request);

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

        when(repository.findByEmailAndTokenAndExpiredAtAfterAndExpired(
                eq(request.getEmail()), eq(request.getToken()), any(LocalDateTime.class), eq(false))
        ).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> service.verifyToken(request));
    }
}
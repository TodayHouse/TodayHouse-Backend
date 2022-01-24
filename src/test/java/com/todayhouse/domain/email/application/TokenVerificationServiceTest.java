package com.todayhouse.domain.email.application;

import com.todayhouse.domain.email.dao.EmailVerificationTokenRepository;
import com.todayhouse.domain.email.domain.EmailVerificationToken;
import com.todayhouse.domain.email.dto.request.TokenVerificationRequest;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class TokenVerificationServiceTest {

    @Autowired
    EmailVerificationTokenRepository repository;

    @Autowired
    TokenVerificationService service;

    @BeforeEach
    void clear(){
        repository.deleteAll();
    }

    @Test
    void 인증코드_확인() {
        String token = "123890";
        String email = "test@email.com";
        TokenVerificationRequest request = TokenVerificationRequest.builder()
                .token(token).email(email).build();
        EmailVerificationToken save = EmailVerificationToken.createEmailToken(email, token);
        repository.save(save);

        EmailVerificationToken result = service.verifyToken(request);

        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getToken()).isEqualTo(token);
        assertThat(result.isExpired()).isEqualTo(true);
        assertThat(result.getExpiredAt()).isAfter(LocalDateTime.now());
        assertThat(result.getExpiredAt()).isBefore(LocalDateTime.now().plusMinutes(3l));

    }
    @Test
    void 인증코드_불일치(){
        String token = "123890";
        String email = "test@email.com";
        TokenVerificationRequest request = TokenVerificationRequest.builder()
                .token("111111").email(email).build();
        EmailVerificationToken save = EmailVerificationToken.createEmailToken(email, token);
        repository.save(save);

        assertThrows(IllegalArgumentException.class, ()->service.verifyToken(request));
    }

    @Test
    void 인증코드_만료(){
        String token = "123890";
        String email = "test@email.com";
        TokenVerificationRequest request = TokenVerificationRequest.builder()
                .token(token).email(email).build();
        EmailVerificationToken save = EmailVerificationToken.createEmailToken(email, token);
        repository.save(save);

        assertThrows(IllegalArgumentException.class, ()->repository.findByEmailAndTokenAndExpiredAtAfterAndExpired(email,token,LocalDateTime.now().plusMinutes(3L),false)
                .orElseThrow(()->new IllegalArgumentException()));
    }


}
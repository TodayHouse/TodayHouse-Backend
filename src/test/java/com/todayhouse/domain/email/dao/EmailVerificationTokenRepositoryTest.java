package com.todayhouse.domain.email.dao;

import com.todayhouse.domain.email.domain.EmailVerificationToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class EmailVerificationTokenRepositoryTest {
    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    EmailVerificationTokenRepository tokenRepository;

    EmailVerificationToken verificationToken =
            EmailVerificationToken.createEmailToken("test@test.com", "123456");

    @Test
    @DisplayName("tokenEntity 저장 후 email로 찾기")
    void findByEmail() {
        tokenRepository.save(verificationToken);

        assertThat(tokenRepository.findByEmail(verificationToken.getEmail()))
                .isEqualTo(Optional.ofNullable(verificationToken));
    }

    @Test
    @DisplayName("유효한 tokenEntity 찾기")
    void findByEmailAndTokenAndExpiredAtAfterAndExpired() {
        testEntityManager.persist(verificationToken);

        assertThat(tokenRepository.findByEmailAndTokenAndExpiredAtAfterAndExpired(
                verificationToken.getEmail(), verificationToken.getToken(), LocalDateTime.now(), false
        )).isEqualTo(Optional.ofNullable(verificationToken));
    }

    @Test
    @DisplayName("인증한 tokenEntity 찾기")
    void findByEmailAndExpired() {
        EmailVerificationToken entity = testEntityManager.persist(verificationToken);
        entity.expireToken();
        testEntityManager.flush();
        testEntityManager.clear();

        assertThat(tokenRepository.findByEmailAndExpired(verificationToken.getEmail(), true));
    }
}
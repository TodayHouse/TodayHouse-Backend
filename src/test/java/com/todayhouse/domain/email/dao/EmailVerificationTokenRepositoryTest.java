package com.todayhouse.domain.email.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.email.domain.EmailVerificationToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class EmailVerificationTokenRepositoryTest extends DataJpaBase {

    @Autowired
    EmailVerificationTokenRepository tokenRepository;

    EmailVerificationToken verificationToken = EmailVerificationToken.createEmailToken("test@test.com", "123456");

    @Test
    @DisplayName("tokenEntity 저장 후 email로 찾기")
    void findByEmail() {
        tokenRepository.save(verificationToken);

        assertThat(tokenRepository.findByEmail(verificationToken.getEmail())).isEqualTo(Optional.ofNullable(verificationToken));
    }

    @Test
    @DisplayName("유효한 tokenEntity 찾기")
    void findByEmailAndTokenAndExpiredAtAfterAndExpired() {
        tokenRepository.save(verificationToken);

        assertThat(tokenRepository.findByEmailAndTokenAndExpiredAtAfterAndExpired(verificationToken.getEmail(), verificationToken.getToken(), LocalDateTime.now(), false)).isEqualTo(Optional.ofNullable(verificationToken));
    }

    @Test
    @DisplayName("인증한 tokenEntity 찾기")
    void findByEmailAndExpired() {
        EmailVerificationToken entity = tokenRepository.save(verificationToken);
        entity.expireToken();

        assertThat(tokenRepository.findByEmailAndExpired(verificationToken.getEmail(), true));
    }

    @Test
    void 토큰_추가_후_변경() {
        String email = "today.house.clone@gmail.com";
        String token = "123776";
        String newToken = "0987621";
        String id = tokenRepository.findByEmail(email).map(unused -> unused.updateToken(token)).orElseGet(() -> tokenRepository.save(EmailVerificationToken.createEmailToken(email, token)).getId());

        Optional<EmailVerificationToken> result = tokenRepository.findById(id);

        assertThat(result.map(t -> t.getId())).isEqualTo(Optional.of(id));
        assertThat(result.map(t -> t.getEmail())).isEqualTo(Optional.of("today.house.clone@gmail.com"));

        //변경
        Optional<Object> prev = result.map(t -> t.getToken());

        id = tokenRepository.findByEmail(email).map(unused -> unused.updateToken(newToken)).orElseGet(() -> tokenRepository.save(EmailVerificationToken.createEmailToken(email, newToken)).getId());
        Optional<EmailVerificationToken> update = tokenRepository.findById(id);

        //검증
        assertThat(tokenRepository.count()).isEqualTo(1);
        assertThat(prev).isNotEqualTo(update.map(t -> t.getToken()));
    }
}
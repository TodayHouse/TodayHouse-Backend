package com.todayhouse.domain.email.application;

import com.todayhouse.domain.email.dao.EmailVerificationTokenRepository;
import com.todayhouse.domain.email.domain.EmailVerificationToken;
import com.todayhouse.domain.email.dto.request.EmailSendRequest;
import com.todayhouse.domain.user.dao.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class EmailSenderServiceTest {

    @Autowired
    EmailVerificationTokenRepository repository;

    @Autowired
    EmailSenderService service;

    @Autowired
    UserRepository userRepository;

    @Test
    void 이미_가입된_이메일(){
        EmailSendRequest request = EmailSendRequest.builder().email("admin")
                .build();
        assertThrows(IllegalArgumentException.class,()->{
            service.sendEmail(request);
        });
    }

    @Test
    void 토큰_추가_후_변경(){
        String email = "today.house.clone@gmail.com";
        String token = "123776";
        String newToken = "0987621";
        String id = repository.findByEmail(email).map(unused -> unused.updateToken(token))
                .orElseGet(() -> repository.save(EmailVerificationToken.createEmailToken(email, token)).getId());

        Optional<EmailVerificationToken> result = repository.findById(id);

        assertThat(result.map(t -> t.getId())).isEqualTo(Optional.of(id));
        assertThat(result.map(t->t.getEmail())).isEqualTo(Optional.of("today.house.clone@gmail.com"));

        //변경
        Optional<Object> prev = result.map(t -> t.getToken());

        id = repository.findByEmail(email).map(unused -> unused.updateToken(newToken))
                .orElseGet(() -> repository.save(EmailVerificationToken.createEmailToken(email, newToken)).getId());
        Optional<EmailVerificationToken> update = repository.findById(id);

        //검증
        assertThat(repository.count()).isEqualTo(1);
        assertThat(prev).isNotEqualTo(update.map(t->t.getToken()));
    }


}
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
    @Transactional
    void 토큰_추가_후_변경(){
        EmailSendRequest request = EmailSendRequest.builder().email("today.house.clone@gmail.com")
                .build();
        String id = service.sendEmail(request);
        Optional<EmailVerificationToken> token = repository.findById(id);
        assertThat(token.map(t -> t.getId())).isEqualTo(Optional.of(id));
        assertThat(token.map(t->t.getEmail())).isEqualTo(Optional.of("today.house.clone@gmail.com"));

        Optional<Object> prev = token.map(t -> t.getToken());
        //변경
        id = service.sendEmail(request);
        Optional<EmailVerificationToken> update = repository.findById(id);
        assertThat(repository.count()).isEqualTo(1);
        assertThat(prev).isNotEqualTo(update.map(t->t.getToken()));
    }


}
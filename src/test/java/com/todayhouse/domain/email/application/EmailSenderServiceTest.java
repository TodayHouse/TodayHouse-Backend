package com.todayhouse.domain.email.application;

import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.email.dao.EmailVerificationTokenRepository;
import com.todayhouse.domain.email.domain.EmailVerificationToken;
import com.todayhouse.domain.email.dto.request.EmailSendRequest;
import com.todayhouse.domain.user.dao.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailSenderServiceTest extends IntegrationBase {

    @Autowired
    EmailVerificationTokenRepository repository;

    @Autowired
    EmailSenderService service;

    @Autowired
    UserRepository userRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @Test
    void 이메일_보내기() {
        String email = "test@test.com";
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        EmailSendRequest request = EmailSendRequest.builder().email(email).build();
        doNothing().when(javaMailSender).send((MimeMessage) any());
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        String token = service.sendEmail(request);

        verify(javaMailSender).send((MimeMessage) any());

        EmailVerificationToken find = repository.findByEmail(email).orElseThrow(IllegalArgumentException::new);

        assertThat(token).isEqualTo(find.getId());
        assertThat(find.getEmail()).isEqualTo(email);
    }

    @Test
    void 토큰_추가_후_변경() {
        String email = "today.house.clone@gmail.com";
        String token = "123776";
        String newToken = "0987621";
        String id = repository.findByEmail(email).map(unused -> unused.updateToken(token))
                .orElseGet(() -> repository.save(EmailVerificationToken.createEmailToken(email, token)).getId());

        Optional<EmailVerificationToken> result = repository.findById(id);

        assertThat(result.map(t -> t.getId())).isEqualTo(Optional.of(id));
        assertThat(result.map(t -> t.getEmail())).isEqualTo(Optional.of("today.house.clone@gmail.com"));

        //변경
        Optional<Object> prev = result.map(t -> t.getToken());

        id = repository.findByEmail(email).map(unused -> unused.updateToken(newToken))
                .orElseGet(() -> repository.save(EmailVerificationToken.createEmailToken(email, newToken)).getId());
        Optional<EmailVerificationToken> update = repository.findById(id);

        //검증
        assertThat(repository.count()).isEqualTo(1);
        assertThat(prev).isNotEqualTo(update.map(t -> t.getToken()));
    }
}
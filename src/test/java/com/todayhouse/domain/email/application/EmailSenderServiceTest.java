package com.todayhouse.domain.email.application;

import com.todayhouse.domain.email.dao.EmailVerificationTokenRepository;
import com.todayhouse.domain.email.domain.EmailVerificationToken;
import com.todayhouse.domain.email.dto.request.EmailSendRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailSenderServiceTest {

    @InjectMocks
    EmailSenderService service;

    @Mock
    JavaMailSender javaMailSender;

    @Mock
    EmailVerificationTokenRepository repository;

    @Test
    void 이메일_처음_보내기() {
        String email = "test@test.com";
        EmailVerificationToken emailToken = EmailVerificationToken.builder()
                .token("123456").email(email).expired(false).id("123456789").build();
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        EmailSendRequest request = EmailSendRequest.builder().email(email).build();
        doNothing().when(javaMailSender).send((MimeMessage) any());
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(repository.findByEmail(email)).thenReturn(Optional.empty());
        when(repository.save(any(EmailVerificationToken.class))).thenReturn(emailToken);

        String token = service.sendEmail(request);

        verify(javaMailSender).send((MimeMessage) any());
        verify(repository).save(any(EmailVerificationToken.class));
        assertThat(token).isEqualTo(emailToken.getId());
    }

    @Test
    void 이메일_재전송() {
        String email = "test@test.com";
        EmailVerificationToken emailToken = EmailVerificationToken.builder()
                .token("123456").email(email).expired(false).id("123456789").build();
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        EmailSendRequest request = EmailSendRequest.builder().email(email).build();
        doNothing().when(javaMailSender).send((MimeMessage) any());
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(repository.findByEmail(email)).thenReturn(Optional.ofNullable(emailToken));

        String token = service.sendEmail(request);

        verify(javaMailSender).send((MimeMessage) any());
        assertThat(token).isEqualTo(emailToken.getId());
    }
}
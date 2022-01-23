package com.todayhouse.domain.email.application;

import com.todayhouse.domain.email.dao.EmailVerificationTokenRepository;
import com.todayhouse.domain.email.domain.EmailVerificationToken;
import com.todayhouse.domain.email.dto.request.EmailSendRequest;
import com.todayhouse.domain.user.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailSenderService {

    private final JavaMailSender javaMailSender;
    private final EmailVerificationTokenRepository repository;
    private final UserRepository userRepository;

    @Async
    public String sendEmail(EmailSendRequest request) {
        String email = request.getEmail();
        String token = createToken();

        // 이미 가입한 이메일 존재
        if(userRepository.existsByEmail(email))
            throw new IllegalArgumentException();

        MimeMessage message = createMessage(email, token);
        javaMailSender.send(message);
        return tokenSave(email, token);
    }

    private String createToken() {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        StringBuffer token = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            token.append(random.nextInt(10));
        }
        return token.toString();
    }

    private MimeMessage createMessage(String email, String token) {
        MimeMessage message = javaMailSender.createMimeMessage();

        String msg = "";
        msg += "<div style='margin:100px;'>";
        msg += "<h2> 안녕하세요 오늘의집입니다. </h2>";
        msg += "<h2>인증코드를 확인해주세요.<h2>";
        msg += "<h1><strong>" + token + "</strong><h1>";
        msg += "<br>";
        msg += "<h3>이메일 인증 절차에 따라 이메일 인증코드를 발급해드립니다." +
                " 인증코드는 이메일 발송 시점으로부터 3분동안 유효합니다.<h3>";
        try {
            message.addRecipients(MimeMessage.RecipientType.TO, email);//보내는 대상
            message.setSubject("[오늘의집] 인증코드");//제목
            message.setText(msg, "utf-8", "html");//내용
            message.setFrom(new InternetAddress(email, "오늘의집"));//보내는 사람
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return message;
    }
    // 토큰 재발급시 업데이트
    private String tokenSave(String email, String token) {
        return repository.findByEmail(email).map(unused -> unused.updateToken(token))
                .orElseGet(()->repository.save(EmailVerificationToken.createEmailToken(email, token)).getId());
    }
}

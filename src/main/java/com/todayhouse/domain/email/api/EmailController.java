package com.todayhouse.domain.email.api;

import com.todayhouse.domain.email.application.EmailSenderService;
import com.todayhouse.domain.email.application.TokenVerificationService;
import com.todayhouse.domain.email.domain.EmailVerificationToken;
import com.todayhouse.domain.email.dto.request.EmailSendRequest;
import com.todayhouse.domain.email.dto.request.TokenVerificationRequest;
import com.todayhouse.domain.email.dto.response.EmailSendResponse;
import com.todayhouse.domain.email.dto.response.TokenVerificationResponse;
import com.todayhouse.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequestMapping("/emails")
@RestController
@RequiredArgsConstructor
public class EmailController {
    private final EmailSenderService emailSenderService;
    private final TokenVerificationService tokenVerificationService;

    @PostMapping("/token/send")
    public BaseResponse sendTokenToEmail(@RequestBody EmailSendRequest request) {
        log.info("이메일 : {}", request.getEmail());
        EmailSendResponse response = new EmailSendResponse(emailSenderService.sendEmail(request));
        return new BaseResponse(response);
    }

    @PutMapping("/token/verify")
    public BaseResponse verifyEmailToken(@RequestBody TokenVerificationRequest request, HttpServletResponse servletResponse) {
        log.info("이메일 : {}, 토큰 : {}", request.getEmail(), request.getToken());
        EmailVerificationToken token = tokenVerificationService.verifyToken(request, servletResponse);
        return new BaseResponse(new TokenVerificationResponse(token));
    }
}

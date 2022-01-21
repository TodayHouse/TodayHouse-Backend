package com.todayhouse.domain.email.api;

import com.todayhouse.domain.email.application.EmailSenderService;
import com.todayhouse.domain.email.dto.request.EmailSendRequest;
import com.todayhouse.domain.email.dto.request.TokenConfirmRequest;
import com.todayhouse.domain.email.dto.response.EmailSendResponse;
import com.todayhouse.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EmailController {
    private final EmailSenderService emailSenderService;

    @PostMapping("/api/email/token/send")
    public BaseResponse sendTokenToEmail(@RequestBody EmailSendRequest request){
        log.info("이메일 : {}", request.getEmail());
        EmailSendResponse response = new EmailSendResponse(emailSenderService.sendEmail(request));
        return new BaseResponse(response);
    }

    @PostMapping("/api/verification/email")
    public BaseResponse confirmEmailToken(@RequestBody TokenConfirmRequest request){
        return new BaseResponse(null);
    }
}

package com.todayhouse.domain.email.application;

import com.todayhouse.domain.email.dao.EmailVerificationTokenRepository;
import com.todayhouse.domain.email.domain.EmailVerificationToken;
import com.todayhouse.domain.email.dto.request.TokenVerificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@Transactional
@RequiredArgsConstructor
public class TokenVerificationService {
    private final EmailVerificationTokenRepository repository;

    public EmailVerificationToken verifyAndDeleteToken(TokenVerificationRequest request){
        //DB에서 해당 토큰을 찾아 삭제
        EmailVerificationToken result = repository.findByEmailAndTokenAndExpirationAtAfter(
                        request.getEmail(), request.getToken(), LocalDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("올바른 인증 코드가 아닙니다."));
        repository.deleteById(result.getId());
        return result;
    }
}

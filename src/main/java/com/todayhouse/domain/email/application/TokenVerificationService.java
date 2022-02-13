package com.todayhouse.domain.email.application;

import com.todayhouse.domain.email.dao.EmailVerificationTokenRepository;
import com.todayhouse.domain.email.domain.EmailVerificationToken;
import com.todayhouse.domain.email.dto.request.TokenVerificationRequest;
import com.todayhouse.domain.email.exception.InvalidEmailTokenException;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.global.config.cookie.CookieUtils;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Collections;


@Service
@Transactional
@RequiredArgsConstructor
public class TokenVerificationService {
    private final EmailVerificationTokenRepository repository;
    private final JwtTokenProvider jwtTokenProvider;

    public EmailVerificationToken verifyToken(TokenVerificationRequest request, HttpServletResponse servletResponse) {
        //DB에서 해당 토큰을 찾아 만료
        EmailVerificationToken result = repository.findByEmailAndTokenAndExpiredAtAfterAndExpired(
                        request.getEmail(), request.getToken(), LocalDateTime.now(), false)
                .orElseThrow(() -> new InvalidEmailTokenException());
        result.expireToken();
        addTokenCookie(request.getEmail(), servletResponse);
        return result;
    }

    private void addTokenCookie(String email, HttpServletResponse response) {
        String token = jwtTokenProvider.createToken(email, Collections.singletonList(Role.GUEST));
        CookieUtils.addNormalCookie(response, "auth_user", CookieUtils.serialize(token), 60 * 60); // 1시간
    }
}

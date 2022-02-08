package com.todayhouse.domain.user.oauth.application;

import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserEmailNotFountException;
import com.todayhouse.domain.user.oauth.dto.request.OAuthSignupRequest;
import com.todayhouse.domain.user.oauth.exception.AuthGuestException;
import com.todayhouse.domain.user.oauth.exception.AuthNotGuestException;
import com.todayhouse.domain.user.oauth.exception.InvalidAuthException;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional(readOnly = true)
    public String findNicknamebyEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserEmailNotFountException());
        return user.getNickname();
    }

    // jwt 발급
    // oauth 인증 후 admin, user 만 발급
    @Override
    @Transactional(readOnly = true)
    public String provideToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserEmailNotFountException());
        if (user.getRoles().contains(Role.GUEST)) {
            throw new AuthGuestException();
        }
        if(!user.getRoles().contains(Role.USER)&&!user.getRoles().contains(Role.ADMIN)){
            throw new InvalidAuthException();
        }
        return jwtTokenProvider.createToken(user.getEmail(), user.getRoles());
    }

    // 인증된 이메일을 회원가입
    @Override
    public User saveGuest(OAuthSignupRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserEmailNotFountException());
        // 인증 받지 않았거나 이미 회원가입한 유저
        if (!user.getRoles().contains(Role.GUEST)) {
            throw new AuthNotGuestException();
        }
        user.oAuthUserUpdate(request);
        return user;
    }
}

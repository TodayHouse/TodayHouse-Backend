package com.todayhouse.domain.user.oauth.application;

import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserEmailNotFountException;
import com.todayhouse.domain.user.oauth.dto.request.OAuthSignupRequest;
import com.todayhouse.domain.user.oauth.exception.AuthGuestException;
import com.todayhouse.domain.user.oauth.exception.AuthNotGuestException;
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

    @Override
    @Transactional(readOnly = true)
    public String provideToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserEmailNotFountException());
        if (user.getRoles().contains(Role.GUEST)) {
            throw new AuthGuestException();
        }
        return jwtTokenProvider.createToken(user.getEmail(), user.getRoles());
    }

    @Override
    public User saveGuest(OAuthSignupRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserEmailNotFountException());
        if (!user.getRoles().contains(Role.GUEST)) {
            throw new AuthNotGuestException();
        }
        user.oAuthUserUpdate(request);
        return user;
    }
}

package com.todayhouse.domain.user.oauth.application;

import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.oauth.dto.request.OAuthSignupRequest;
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
                .orElseThrow(() -> new IllegalArgumentException("이메일을 찾을 수 없습니다."));
        return user.getNickname();
    }

    @Override
    @Transactional(readOnly = true)
    public String provideToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일을 찾을 수 없습니다."));
        if (user.getRoles().contains(Role.GUEST.getKey())) {
            throw new IllegalArgumentException("회원가입하지 않은 사용자입니다.");
        }
        return jwtTokenProvider.createToken(user.getEmail(), user.getRoles());
    }

    @Override
    public User saveGuest(OAuthSignupRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일을 찾을 수 없습니다."));
        if (!user.getRoles().contains(Role.GUEST.getKey())) {
            throw new IllegalArgumentException("이미 회원가입한 이메일입니다.");
        }
        user.oAuthUserUpdate(request);
        return user;
    }
}

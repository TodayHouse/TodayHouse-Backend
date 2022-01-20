package com.todayhouse.domain.user.application;

import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.dto.request.UserLoginRequest;
import com.todayhouse.domain.user.dto.request.UserSaveRequest;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public User save(UserSaveRequest request) {
        return userRepository.save(request.toEntity());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Override
    @Transactional(readOnly = true)
    public String login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        return jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
    }

    //테스트 계정
    @PostConstruct
    private void preMember() {
        userRepository.save(User.builder()
                .email("admin")
                .password(new BCryptPasswordEncoder().encode("12345"))
                .roles(Collections.singletonList("ROLE_ADMIN"))
                .agreePICU(true)
                .agreePromotion(true)
                .agreeTOS(true)
                .nickname("admin")
                .build());

        userRepository.save(User.builder()
                .email("a@a.com")
                .password(new BCryptPasswordEncoder().encode("12345"))
                .roles(Collections.singletonList("ROLE_USER"))
                .agreePICU(true)
                .agreePromotion(true)
                .agreeTOS(true)
                .nickname("user1")
                .build());
    }
}

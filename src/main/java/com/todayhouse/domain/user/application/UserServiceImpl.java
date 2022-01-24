package com.todayhouse.domain.user.application;

import com.todayhouse.domain.email.dao.EmailVerificationTokenRepository;
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

@Transactional
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

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
    public User saveUser(UserSaveRequest request) {
        emailVerificationTokenRepository.findByEmailAndExpired(request.getEmail(),true)
                        .orElseThrow(()->new IllegalArgumentException("이메일 인증이 필요합니다."));
        saveRequestValidate(request);
        return userRepository.save(request.toEntity());
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

    private void saveRequestValidate(UserSaveRequest request){
        if(userRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("중복된 이메일입니다.");

        if(userRepository.existsByNickname(request.getNickname()))
            throw new IllegalArgumentException("중복된 닉네임입니다.");

        if(!request.getPassword1().equals(request.getPassword2()))
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    //테스트 계정
    @PostConstruct
    private void preMember() {
        userRepository.save(User.builder()
                .email("admin")
                .password(new BCryptPasswordEncoder().encode("12345678"))
                .roles(Collections.singletonList("ROLE_ADMIN"))
                .agreePICU(true)
                .agreePromotion(true)
                .agreeTOS(true)
                .nickname("admin")
                .build());

        userRepository.save(User.builder()
                .email("a@a.com")
                .password(new BCryptPasswordEncoder().encode("12345678"))
                .roles(Collections.singletonList("ROLE_USER"))
                .agreePICU(true)
                .agreePromotion(true)
                .agreeTOS(true)
                .nickname("user1")
                .build());
    }
}

package com.todayhouse.domain.user.application;

import com.todayhouse.domain.email.dao.EmailVerificationTokenRepository;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Agreement;
import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.dto.request.PasswordUpdateRequest;
import com.todayhouse.domain.user.dto.request.UserLoginRequest;
import com.todayhouse.domain.user.dto.request.UserSignupRequest;
import com.todayhouse.domain.user.exception.*;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
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
    public User saveUser(UserSignupRequest request) {
        if (emailVerificationTokenRepository.findByEmailAndExpired(request.getEmail(), true)
                .isEmpty()) {
            throw new UserEmailNotAuthException();
        }
        // 중복 회원가입, request 유효성 검사
        validateSignupRequest(request);
        return userRepository.save(request.toEntity());
    }

    @Override
    @Transactional(readOnly = true)
    public String login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(UserNotFoundException::new);
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new WrongPasswordException();
        }

        return jwtTokenProvider.createToken(user.getEmail(), user.getRoles());
    }

    @Override
    public void updatePassword(String email, PasswordUpdateRequest request) {
        if(!request.getPassword1().equals(request.getPassword2()))
            throw new SignupPasswordException();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        user.updatePassword(request.getPassword1());
    }

    private void validateSignupRequest(UserSignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new UserEmailExistExcecption();

        if (userRepository.existsByNickname(request.getNickname()))
            throw new UserNicknameExistException();

        if (!request.getPassword1().equals(request.getPassword2()))
            throw new SignupPasswordException();
    }

    //테스트 계정
    @PostConstruct
    private void preMember() {
        userRepository.save(User.builder()
                .authProvider(AuthProvider.LOCAL)
                .email("admin@admin.com")
                .password(new BCryptPasswordEncoder().encode("today123"))
                .roles(Collections.singletonList(Role.ADMIN))
                .agreement(Agreement.agreeAll())
                .nickname("admin")
                .build());

        userRepository.save(User.builder()
                .authProvider(AuthProvider.LOCAL)
                .email("a@a.com")
                .password(new BCryptPasswordEncoder().encode("abc12345"))
                .roles(Collections.singletonList(Role.USER))
                .agreement(Agreement.agreeAll())
                .nickname("user1")
                .build());
    }
}

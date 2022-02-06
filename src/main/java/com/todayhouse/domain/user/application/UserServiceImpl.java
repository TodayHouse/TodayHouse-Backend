package com.todayhouse.domain.user.application;

import com.todayhouse.domain.email.dao.EmailVerificationTokenRepository;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
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
        signupRequestValidate(request);
        return userRepository.save(request.toEntity());
    }

    @Override
    @Transactional(readOnly = true)
    public String login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserEmailNotFountException());
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new WrongPasswordException();
        }

        return jwtTokenProvider.createToken(user.getEmail(), user.getRoles());
    }

    private void signupRequestValidate(UserSignupRequest request) {
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
                .email("admin")
                .password(new BCryptPasswordEncoder().encode("12345678"))
                .roles(Collections.singletonList(Role.ADMIN))
                .agreePICU(true)
                .agreePromotion(true)
                .agreeTOS(true)
                .nickname("admin")
                .build());

        userRepository.save(User.builder()
                .authProvider(AuthProvider.LOCAL)
                .email("a@a.com")
                .password(new BCryptPasswordEncoder().encode("12345678"))
                .roles(Collections.singletonList(Role.USER))
                .agreePICU(true)
                .agreePromotion(true)
                .agreeTOS(true)
                .nickname("user1")
                .build());
    }
}

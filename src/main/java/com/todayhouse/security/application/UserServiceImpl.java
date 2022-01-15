package com.todayhouse.security.application;

import com.todayhouse.security.config.JwtTokenProvider;
import com.todayhouse.security.domian.dto.Login;
import com.todayhouse.security.domian.dto.Save;
import com.todayhouse.security.domian.user.User;
import com.todayhouse.security.domian.user.UserRepository;
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
public class UserServiceImpl implements UserService{
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public User save(Save.UserSaveRequest request) {
        return userRepository.save(User.builder()
                .email(request.getEmail())
                .password(new BCryptPasswordEncoder().encode(request.getPassword()))
                .roles(Collections.singletonList("ROLE_USER")).build());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public String login(Login.UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new IllegalArgumentException("이메일을 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }

        return jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
    }

    //테스트 계정
    @PostConstruct
    private void preMember(){
        userRepository.save(User.builder()
                .email("admin")
                .password(String.valueOf(new BCryptPasswordEncoder().encode("12345")))
                .roles(Collections.singletonList("ROLE_ADMIN"))
                .build());

        userRepository.save(User.builder()
                .email("a@a.com")
                .password(String.valueOf(new BCryptPasswordEncoder().encode("12345")))
                .roles(Collections.singletonList("ROLE_USER"))
                .build());
    }
}

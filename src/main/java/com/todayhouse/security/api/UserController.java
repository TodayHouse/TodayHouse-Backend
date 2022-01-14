package com.todayhouse.security.api;

import com.todayhouse.security.config.JwtTokenProvider;
import com.todayhouse.security.domian.user.User;
import com.todayhouse.security.domian.user.UserRepository;
import com.todayhouse.security.domian.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    // 회원가입
    @PostMapping("/join")
    public BaseResponse join(@RequestBody Map<String, String> user) {
        String saveEmail = userRepository.save(User.builder()
                .email(user.get("email"))
                .password(passwordEncoder.encode(user.get("password")))
                .roles(Collections.singletonList("ROLE_USER")) // 최초 가입시 USER 로 설정
                .build()).getEmail();

        return new BaseResponse(true, 0, "Success", saveEmail);
    }

    // 로그인
    @PostMapping("/login")
    public BaseResponse login(@RequestBody Map<String, String> user) {
        User member = userRepository.findByEmail(user.get("email"))
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
        if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        return new BaseResponse(true, 0, "Success", jwtTokenProvider.createToken(member.getUsername(), member.getRoles()));
    }

    //jwt확인용
    @GetMapping("/test")
    public List<User> test(){
        List<User> all = userRepository.findAll();
        return all;
    }

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


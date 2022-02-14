package com.todayhouse.domain.user.api;

import com.todayhouse.domain.user.application.UserService;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.dto.request.PasswordUpdateRequest;
import com.todayhouse.domain.user.dto.request.UserLoginRequest;
import com.todayhouse.domain.user.dto.request.UserSignupRequest;
import com.todayhouse.domain.user.dto.response.UserFindResponse;
import com.todayhouse.domain.user.dto.response.UserLoginResponse;
import com.todayhouse.domain.user.dto.response.UserSignupResponse;
import com.todayhouse.domain.user.exception.UserEmailNotFountException;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.global.config.cookie.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/emails/{email}")
    public BaseResponse findUser(@PathVariable String email) {
        User user = userService.findByEmail(email).orElseThrow(UserEmailNotFountException::new);
        return new BaseResponse(new UserFindResponse(user));
    }

    @GetMapping("/emails/{email}/exist")
    public BaseResponse existEmail(@PathVariable String email) {
        log.info("확인 이메일: {}", email);
        boolean exist = userService.existByEmail(email);
        return new BaseResponse(exist);
    }

    @GetMapping("/nicknames/{nickname}/exist")
    public BaseResponse existNickname(@PathVariable String nickname) {
        log.info("확인 닉네임: {}", nickname);
        boolean exist = userService.existByNickname(nickname);
        return new BaseResponse(exist);
    }

    // 회원가입
    @PostMapping("/signup")
    public BaseResponse signup(@Valid @RequestBody UserSignupRequest request) {
        log.info("이메일: {}, 닉네임: {}",
                request.getEmail(), request.getNickname());
        UserSignupResponse response = new UserSignupResponse(userService.saveUser(request));
        return new BaseResponse(response);
    }

    // 로그인
    @PostMapping("/login")
    public BaseResponse login(@Valid @RequestBody UserLoginRequest request) {
        UserLoginResponse response = new UserLoginResponse(userService.login(request));
        return new BaseResponse(response);
    }

    @PutMapping("/password/new")
    public BaseResponse updatePassword(@RequestBody PasswordUpdateRequest request, Principal principal,
                                       HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        userService.updatePassword(principal.getName(), request);
        CookieUtils.deleteCookie(servletRequest, servletResponse, "auth_user");
        return new BaseResponse();
    }

    //api test용
    @GetMapping("/test")
    public String test() {
        return "hello test";
    }
}

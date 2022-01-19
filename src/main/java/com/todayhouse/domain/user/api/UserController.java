package com.todayhouse.domain.user.api;

import com.todayhouse.domain.user.application.UserService;
import com.todayhouse.domain.user.dto.request.UserLoginRequest;
import com.todayhouse.domain.user.dto.request.UserSaveRequest;
import com.todayhouse.domain.user.dto.response.UserLoginResponse;
import com.todayhouse.domain.user.dto.response.UserSaveResponse;
import com.todayhouse.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/join")
    public BaseResponse join(@RequestBody UserSaveRequest request) {
        UserSaveResponse response = new UserSaveResponse(userService.save(request));
        return new BaseResponse(response);
    }

    // 로그인
    @PostMapping("/login")
    public BaseResponse login(@RequestBody UserLoginRequest request) {
        UserLoginResponse response = new UserLoginResponse(userService.login(request));
        return new BaseResponse(response);
    }

    //api test용
    @GetMapping("/test")
    public String test(){
        return "hello test";
    }
}

package com.todayhouse.security.api;

import com.todayhouse.security.application.UserService;
import com.todayhouse.security.domian.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/join")
    public BaseResponse join(@RequestBody Save.UserSaveRequest request) {
        Save.UserSaveResponse response = new Save.UserSaveResponse(userService.save(request));
        return new BaseResponse(true, 0, "Success", response);
    }

    // 로그인
    @PostMapping("/login")
    public BaseResponse login(@RequestBody Login.UserLoginRequest request) {
        Login.UserLoginResponse response = new Login.UserLoginResponse(userService.login(request));
        return new BaseResponse(true, 0, "Success", response);
    }

    //api test용
    @GetMapping("/test")
    public String test(){
        return "hello test";
    }
}


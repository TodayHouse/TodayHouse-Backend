package com.todayhouse.domain.user.api;

import com.todayhouse.domain.user.application.UserService;
import com.todayhouse.domain.user.dto.request.UserLoginRequest;
import com.todayhouse.domain.user.dto.request.UserSaveRequest;
import com.todayhouse.domain.user.dto.response.UserLoginResponse;
import com.todayhouse.domain.user.dto.response.UserSaveResponse;
import com.todayhouse.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/email/{email}/exist")
    public BaseResponse existEmail(@PathVariable String email){
        log.info("확인 이메일: {}",email);
        boolean exist = userService.existByEmail(email);
        return new BaseResponse(exist);
    }

    @GetMapping("/nickname/{nickname}/exist")
    public BaseResponse existNickname(@PathVariable String nickname){
        log.info("확인 닉네임: {}",nickname);
        boolean exist = userService.existByNickname(nickname);
        return new BaseResponse(exist);
    }

    // 회원가입
    @PostMapping("/signup")
    public BaseResponse signup(@Valid @RequestBody UserSaveRequest request) {
        log.info("이메일: {}, 닉네임: {}",
                request.getEmail(), request.getNickname());
        UserSaveResponse response = new UserSaveResponse(userService.saveUser(request));
        return new BaseResponse(response);
    }

    // 로그인
    @PostMapping("/login")
    public BaseResponse login(@Valid @RequestBody UserLoginRequest request) {
        UserLoginResponse response = new UserLoginResponse(userService.login(request));
        return new BaseResponse(response);
    }

    //api test용
    @GetMapping("/test")
    public String test(){
        return "hello test";
    }
}

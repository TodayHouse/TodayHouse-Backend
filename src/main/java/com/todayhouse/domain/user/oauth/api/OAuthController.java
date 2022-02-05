package com.todayhouse.domain.user.oauth.api;

import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.oauth.application.OAuthService;
import com.todayhouse.domain.user.oauth.dto.request.OAuthSignupRequest;
import com.todayhouse.domain.user.oauth.dto.response.OAuthSignupInfoResponse;
import com.todayhouse.domain.user.oauth.dto.response.OAuthSignupResponse;
import com.todayhouse.domain.user.oauth.dto.response.OAuthTokenResponse;
import com.todayhouse.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/oauth2")
@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/signup/info")
    public BaseResponse signupInfo(Authentication authentication) {
        String email = ((User) authentication.getPrincipal()).getEmail();
        String nickname = oAuthService.findNicknamebyEmail(email);
        OAuthSignupInfoResponse response = OAuthSignupInfoResponse.builder()
                .email(email).nickname(nickname).build();
        return new BaseResponse(response);
    }

    @GetMapping("/token")
    public BaseResponse provideToken(Authentication authentication) {
        String email = ((User) authentication.getPrincipal()).getEmail();
        return new BaseResponse(new OAuthTokenResponse(oAuthService.provideToken(email)));
    }

    @PutMapping("/signup")
    public BaseResponse signup(@RequestBody OAuthSignupRequest request) {
        User user = oAuthService.saveGuest(request);
        return new BaseResponse(new OAuthSignupResponse(user));
    }
}

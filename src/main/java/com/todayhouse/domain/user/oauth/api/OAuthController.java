package com.todayhouse.domain.user.oauth.api;

import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.oauth.application.OAuthService;
import com.todayhouse.domain.user.oauth.dto.request.OAuthSignupRequest;
import com.todayhouse.domain.user.oauth.dto.response.OAuthSignupInfoResponse;
import com.todayhouse.domain.user.oauth.dto.response.OAuthSignupResponse;
import com.todayhouse.domain.user.oauth.dto.response.OAuthTokenResponse;
import com.todayhouse.domain.user.oauth.exception.InvalidAuthException;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.global.config.oauth.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestMapping("/oauth2")
@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/email")
    public BaseResponse signupInfo(Authentication authentication) {
        String email = ((User) authentication.getPrincipal()).getEmail();
        OAuthSignupInfoResponse response = OAuthSignupInfoResponse.builder()
                .email(email).build();
        return new BaseResponse(response);
    }

    @GetMapping("/token")
    public BaseResponse provideToken(Authentication authentication) {
        String email = ((User) authentication.getPrincipal()).getEmail();
        return new BaseResponse(new OAuthTokenResponse(oAuthService.provideToken(email)));
    }

    @PutMapping("/signup")
    public BaseResponse signup(@RequestBody OAuthSignupRequest request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        String jwt = CookieUtils.getCookie(servletRequest, "auth_user")
                .map(cookie -> CookieUtils.deserialize(cookie, String.class))
                .orElseThrow(()->new InvalidAuthException());
        
        User user = oAuthService.saveGuest(request, jwt);
        CookieUtils.deleteCookie(servletRequest, servletResponse, "auth_user");
        return new BaseResponse(new OAuthSignupResponse(user));
    }
}

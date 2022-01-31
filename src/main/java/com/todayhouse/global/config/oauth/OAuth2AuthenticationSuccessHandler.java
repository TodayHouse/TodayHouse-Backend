package com.todayhouse.global.config.oauth;

import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.oauth.dao.HttpCookieOAuth2AuthorizationRequestRepository;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.todayhouse.domain.user.oauth.dao.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    private final String SNS_SIGNUP_URL = "http://localhost:3000/sns";

    @Value("${oauth.authorizedRedirectUris}")
    List<String> authorizedRedirectUris;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);
        log.info("성공 요청 url : {}", targetUrl);
        if (response.isCommitted()) {
            log.debug("요청이 이미 완료되었습니다. " + targetUrl + "로 리다이렉트할 수 없습니다.");
            return;
        }
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);
        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get()))
            throw new IllegalArgumentException("unauthorized Redirect URI");

        String email = (String) ((OAuth2User)authentication.getPrincipal()).getAttributes().get("email");

        // 추가 회원가입(닉네임, 약관동의)
        User findUser = userRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("이메일을 찾을 수 없습니다."));
        if(!findUser.isSignedUp()){
            return UriComponentsBuilder.fromUriString(SNS_SIGNUP_URL)
                    .queryParam("email", findUser.getEmail())
                    .queryParam("nickname",findUser.getNickname())
                    .build().toUriString();
        }
        String targetUri = redirectUri.orElse("/oauth/redirect");
        System.out.println(targetUri);
        String token = tokenProvider.createToken(email, Collections.singletonList(Role.USER.getKey()));
        return UriComponentsBuilder.fromUriString(targetUri)
                .build().toUriString();
    }

    // 쿠기 삭제
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    // 리다이렉트 uri가 맞는지 확인
    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        return authorizedRedirectUris.stream()
                .anyMatch(authorizedRedirectUri -> {
//                    지정된 uri, port로만 redirect
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }
                    return false;
                });
    }
}
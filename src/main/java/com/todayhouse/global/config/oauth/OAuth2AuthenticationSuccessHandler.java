package com.todayhouse.global.config.oauth;

import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.oauth.dao.HttpCookieOAuth2AuthorizationRequestRepository;
import com.todayhouse.domain.user.oauth.dto.OAuthAttributes;
import com.todayhouse.domain.user.oauth.exception.InvalidRedirectUriException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.todayhouse.domain.user.oauth.dao.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    private final String SNS_SIGNUP_URL = "http://localhost:3000/social_signup";
    private final String MAIN_URL = "http://localhost:3000";

    @Value("${oauth.authorizedRedirectUris}")
    List<String> authorizedRedirectUris;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = determineCookieAndTargetUrl(request, response, authentication);
        log.info("인증 성공 target url : {}", targetUrl);
        if (response.isCommitted()) {
            log.debug("요청이 이미 완료되었습니다. " + targetUrl + "로 리다이렉트할 수 없습니다.");
            return;
        }
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineCookieAndTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);
        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get()))
            throw new InvalidRedirectUriException();

        String targetUri = redirectUri.orElse(MAIN_URL);

        List<Role> roles = authentication.getAuthorities().stream().map(auth -> Role.keyToRole(auth.getAuthority()))
                .collect(Collectors.toList());
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        OAuthAttributes oAuthAttributes = getAttributeFromOAuth2User(oAuth2User);
        if (roles.contains(Role.GUEST)) { // 추가회원가입 필요 -> 임시 인증 쿠키, uri 변경
            String jwt = tokenProvider.createOAuthToken(oAuthAttributes.getEmail(), roles,
                    oAuthAttributes.getAuthProvider(), oAuthAttributes.getPicture(), oAuthAttributes.getNickname());
            CookieUtils.addHttpOnlyCookie(response, "auth_user", CookieUtils.serialize(jwt), 60 * 60);
            targetUri = SNS_SIGNUP_URL;
        } else { // 로그인 -> jwt
            String jwt = tokenProvider.createToken(oAuthAttributes.getEmail(), roles);
            CookieUtils.addNormalCookie(response, "access_token", jwt, 10);
        }

        return UriComponentsBuilder.fromUriString(targetUri)
                .build().toUriString();
    }

    // OAuth2User를 OAuthAttribute로 변환
    private OAuthAttributes getAttributeFromOAuth2User(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        if (attributes.get("email") != null) {
            Map<String, Object> wrapper = new HashMap<>();
            wrapper.put("response", attributes);
            return OAuthAttributes.of("naver", "id", wrapper);
        } else {
            return OAuthAttributes.of("kakao", "id", attributes);
        }
    }

    // HttpCookie 삭제
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
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}
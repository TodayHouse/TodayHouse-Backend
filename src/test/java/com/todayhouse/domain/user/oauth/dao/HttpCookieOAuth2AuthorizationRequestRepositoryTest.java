package com.todayhouse.domain.user.oauth.dao;

import com.todayhouse.global.config.oauth.CookieUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import javax.servlet.http.Cookie;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class HttpCookieOAuth2AuthorizationRequestRepositoryTest {

    @InjectMocks
    HttpCookieOAuth2AuthorizationRequestRepository repository;

    @Test
    @DisplayName("oauth2_auth_request 쿠키를 확인")
    void loadAuthorizationRequest() {
        OAuth2AuthorizationRequest authRequest = OAuth2AuthorizationRequest.authorizationCode().clientId("id").authorizationUri("test").build();
        Cookie[] cookies = new Cookie[]{
                new Cookie("oauth2_auth_request", CookieUtils.serialize(authRequest))
        };
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setCookies(cookies);

        OAuth2AuthorizationRequest result = repository.loadAuthorizationRequest(servletRequest);

        assertThat(result.getAuthorizationUri()).isEqualTo("test");
    }

    @Test
    @DisplayName("OAuth2AuthorizationRequest를 쿠키로 저장")
    void saveAuthorizationRequest() {
        OAuth2AuthorizationRequest authRequest = OAuth2AuthorizationRequest.authorizationCode().clientId("id").authorizationUri("test").build();
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();

        repository.saveAuthorizationRequest(authRequest, servletRequest, servletResponse);

        assertThat(servletResponse.getCookie("oauth2_auth_request").getValue()).isEqualTo(CookieUtils.serialize(authRequest));
    }

    @Test
    @DisplayName("OAuth2AuthorizationRequest가 없으면 관련 쿠키 삭제")
    void saveAuthorizationRequest2() {
        String redirect_uri = "redirect.com";
        Cookie cookie1 = new Cookie("oauth2_auth_request", CookieUtils.serialize("aaa"));
        Cookie cookie2 = new Cookie("redirect_uri", CookieUtils.serialize(redirect_uri));
        Cookie[] cookies = new Cookie[]{
                cookie1, cookie2
        };
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setCookies(cookies);
        servletResponse.addCookie(cookie1);
        servletResponse.addCookie(cookie2);

        repository.saveAuthorizationRequest(null, servletRequest, servletResponse);

        assertThat(servletResponse.getCookie("oauth2_auth_request").getValue()).isEqualTo("");
        assertThat(servletResponse.getCookie("redirect_uri").getValue()).isEqualTo("");
    }

    @Test
    @DisplayName("OAuth2AuthorizationRequest와 같다")
    void removeAuthorizationRequest() {
        OAuth2AuthorizationRequest authRequest = OAuth2AuthorizationRequest.authorizationCode().clientId("id").authorizationUri("test").build();
        Cookie[] cookies = new Cookie[]{
                new Cookie("oauth2_auth_request", CookieUtils.serialize(authRequest))
        };
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setCookies(cookies);

        OAuth2AuthorizationRequest result = repository.removeAuthorizationRequest(servletRequest);

        assertThat(result.getAuthorizationUri()).isEqualTo("test");
    }

    @Test
    @DisplayName("oauth2_auth_request, redirect_uri 쿠키를 삭제")
    void removeAuthorizationRequestCookies() {
        OAuth2AuthorizationRequest authRequest = OAuth2AuthorizationRequest.authorizationCode().clientId("id").authorizationUri("test").build();
        String redirect_uri = "redirect.com";
        Cookie cookie1 = new Cookie("oauth2_auth_request", CookieUtils.serialize(authRequest));
        Cookie cookie2 = new Cookie("redirect_uri", CookieUtils.serialize(redirect_uri));
        Cookie[] cookies = new Cookie[]{
                cookie1, cookie2
        };
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setCookies(cookies);
        servletResponse.addCookie(cookie1);
        servletResponse.addCookie(cookie2);

        repository.removeAuthorizationRequestCookies(servletRequest, servletResponse);

        assertThat(servletResponse.getCookie("oauth2_auth_request").getValue()).isEqualTo("");
        assertThat(servletResponse.getCookie("redirect_uri").getValue()).isEqualTo("");
    }
}
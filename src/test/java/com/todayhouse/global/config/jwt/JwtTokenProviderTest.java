package com.todayhouse.global.config.jwt;

import com.todayhouse.domain.user.domain.Agreement;
import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.config.cookie.CookieUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenProvider, "secretKey", "abcabc123");
        ReflectionTestUtils.setField(tokenProvider, "expiration", 1000000L);
        ReflectionTestUtils.setField(tokenProvider, "guestExpiration", 1000000L);
    }

    @Test
    void token_검증() {
        //given
        ReflectionTestUtils.setField(tokenProvider, "expiration", 1000000L);
        List<Role> roles = new ArrayList<>();
        roles.add(Role.USER);
        roles.add(Role.ADMIN);

        User user = User.builder()
                .authProvider(AuthProvider.LOCAL)
                .email("test@test.com")
                .password(new BCryptPasswordEncoder().encode("12345678"))
                .roles(roles)
                .agreement(Agreement.agreeAll())
                .nickname("testname")
                .build();
        String jwt = tokenProvider.createToken(user.getEmail(), roles);

        //when,then
        assertThat(tokenProvider.validateToken(jwt)).isTrue();
        assertThat(tokenProvider.getUserPk(jwt)).isEqualTo("test@test.com");

        Authentication authentication = tokenProvider.getAuthentication(jwt);
        User findUser = (User) authentication.getPrincipal();
        assertThat(findUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(findUser.getRoles()).isEqualTo(user.getRoles());
    }

    @Test
    void 만료된_jwt() throws InterruptedException {
        ReflectionTestUtils.setField(tokenProvider, "expiration", 0L);
        String jwt = tokenProvider.createToken("test", Collections.singletonList(Role.USER));

        Thread.sleep(1);

        assertThat(tokenProvider.validateToken(jwt)).isFalse();
    }

    @Test
    void request_header에_jwt() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String jwt = tokenProvider.createToken("a", Collections.singletonList(Role.GUEST));
        request.addHeader("Authorization", "Bearer " + jwt);

        assertThat(tokenProvider.resolveToken(request)).isEqualTo(jwt);
    }

    @Test
    void request_cookie에_jwt() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String jwt = tokenProvider.createToken("a", Collections.singletonList(Role.GUEST));
        Cookie[] cookies = new Cookie[]{
                new Cookie("auth_user", CookieUtils.serialize(jwt))
        };
        request.setCookies(cookies);

        assertThat(tokenProvider.resolveToken(request)).isEqualTo(jwt);
    }

    @Test
    void oAuthToken_검증() {
        User user = User.builder().email("test@test.com").profileImage("http://a.jpg")
                .authProvider(AuthProvider.KAKAO).nickname("hello")
                .roles(Collections.singletonList(Role.GUEST)).build();

        String jwt = tokenProvider.createOAuthToken(user.getEmail(), user.getRoles(),
                user.getAuthProvider(), user.getProfileImage(), user.getNickname());

        assertThat(tokenProvider.validateToken(jwt)).isTrue();
        assertThat(tokenProvider.getUserPk(jwt)).isEqualTo(user.getEmail());
        User getUser = (User) tokenProvider.getAuthentication(jwt).getPrincipal();
        assertThat(getUser.getRoles()).isEqualTo(user.getRoles());
        assertThat(getUser.getAuthProvider()).isEqualTo(user.getAuthProvider());
        assertThat(getUser.getNickname()).isEqualTo(user.getNickname());
        assertThat(getUser.getProfileImage()).isEqualTo(user.getProfileImage());
    }
}
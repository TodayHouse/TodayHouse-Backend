package com.todayhouse.global.config.jwt;

import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.config.oauth.CookieUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    JwtTokenProvider tokenProvider;

    @Mock
    UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenProvider, "secretKey", "abcabc123");
    }

    @Test
    void jwt_검증() {
        //given
        ReflectionTestUtils.setField(tokenProvider, "expiration", 1000000L);
        List<String> roles = new ArrayList<>();
        roles.add(Role.USER.getKey());
        roles.add(Role.ADMIN.getKey());

        User user = User.builder()
                .authProvider(AuthProvider.local)
                .email("test@test.com")
                .password(new BCryptPasswordEncoder().encode("12345678"))
                .roles(roles)
                .agreePICU(true)
                .agreePromotion(true)
                .agreeTOS(true)
                .nickname("testname")
                .build();
        String jwt = tokenProvider.createToken(user.getEmail(), roles);
        when(userDetailsService.loadUserByUsername("test@test.com")).thenReturn(user);

        //when,then
        assertThat(tokenProvider.validateToken(jwt)).isTrue();
        assertThat(tokenProvider.getUserPk(jwt)).isEqualTo("test@test.com");

        Authentication authentication = tokenProvider.getAuthentication(jwt);
        User findUser = (User) authentication.getPrincipal();
        assertThat(findUser.getNickname()).isEqualTo(user.getNickname());
        assertThat(findUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(findUser.getAuthorities()).isEqualTo(user.getAuthorities());
        assertThat(findUser.getAuthProvider()).isEqualTo(user.getAuthProvider());
    }

    @Test
    void 만료된_jwt() throws InterruptedException {
        ReflectionTestUtils.setField(tokenProvider, "expiration", 0L);
        String jwt = tokenProvider.createToken("test", Collections.singletonList(Role.USER.getKey()));

        Thread.sleep(1);

        assertThat(tokenProvider.validateToken(jwt)).isFalse();
    }

    @Test
    void request_header에_jwt() {
        ReflectionTestUtils.setField(tokenProvider, "expiration", 1000000L);
        MockHttpServletRequest request = new MockHttpServletRequest();
        String jwt = tokenProvider.createToken("a", Collections.singletonList(Role.GUEST.getKey()));
        request.addHeader("Authorization", "Bearer " + jwt);

        assertThat(tokenProvider.resolveToken(request)).isEqualTo(jwt);
    }

    @Test
    void request_cookie에_jwt() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String jwt = tokenProvider.createToken("a", Collections.singletonList(Role.GUEST.getKey()));
        Cookie[] cookies = new Cookie[]{
                new Cookie("auth_user", CookieUtils.serialize(jwt))
        };
        request.setCookies(cookies);

        assertThat(tokenProvider.resolveToken(request)).isEqualTo(jwt);
    }
}
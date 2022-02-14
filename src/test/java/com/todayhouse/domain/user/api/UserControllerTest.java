package com.todayhouse.domain.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.email.dao.EmailVerificationTokenRepository;
import com.todayhouse.domain.email.domain.EmailVerificationToken;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Agreement;
import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.dto.request.PasswordUpdateRequest;
import com.todayhouse.domain.user.dto.request.UserSignupRequest;
import com.todayhouse.global.config.cookie.CookieUtils;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.Cookie;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest extends IntegrationBase {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    EmailVerificationTokenRepository emailVerificationTokenRepository;

    @MockBean
    Principal principal;

    @BeforeEach
    void clearRepository() {
        userRepository.deleteAll();
        userRepository.save(User.builder()
                .authProvider(AuthProvider.LOCAL)
                .email("test@test.com")
                .password(new BCryptPasswordEncoder().encode("abc12345"))
                .roles(Collections.singletonList(Role.USER))
                .agreement(Agreement.agreeAll())
                .nickname("testname")
                .build());
    }

    @Test
    void 회원가입() throws Exception {
        String email = "today.house.clone@gmail.com";
        String token = "101010";
        UserSignupRequest request = UserSignupRequest.builder()
                .email(email)
                .password1("09876543")
                .password2("09876543")
                .nickname("test")
                .agreePICU(true)
                .agreePromotion(true)
                .agreeTOS(true).build();
        String url = "http://localhost:8080/users/signup";
        String jwt = jwtTokenProvider.createToken(email, Collections.singletonList(Role.GUEST));
        Cookie cookie = new Cookie("auth_user", CookieUtils.serialize(jwt));

        EmailVerificationToken emailToken = EmailVerificationToken.createEmailToken(email, token);
        emailToken.expireToken();
        emailVerificationTokenRepository.save(emailToken);

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 로그인() throws Exception {
        Map<String, String> user = new HashMap<>();
        user.put("email", "test@test.com");
        user.put("password", "abc12345");
        String url = "http://localhost:8080/users/login";

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void jwtTest() throws Exception {
        String url = "http://localhost:8080/users/test";
        String jwt = jwtTokenProvider.createToken("test", Collections.singletonList(Role.USER));

        mockMvc.perform(get(url)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 이메일_닉네임_중복() throws Exception {
        String url = "http://localhost:8080/users/";

        //email
        mockMvc.perform(get(url + "emails/test@test.com/exist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true));
        mockMvc.perform(get(url + "emails/fail@test.com/exist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(false));

        //nickname
        mockMvc.perform(get(url + "nicknames/testname/exist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true));
        mockMvc.perform(get(url + "nicknames/testfail/exist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(false));
    }

    @Test
    void 비밀번호_변경() throws Exception {
        String email = "test@test.com";
        String url = "http://localhost:8080/users/password/new";
        String token = jwtTokenProvider.createToken(email, Collections.singletonList(Role.GUEST));
        Cookie cookie = new Cookie("auth_user", CookieUtils.serialize(token));
        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
                .password1("abcdefg1").password2("abcdefg1")
                .build();
        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request))
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("auth_user", 0))
                .andReturn();

        User user = userRepository.findByEmail("test@test.com").orElse(null);
        assertThat(new BCryptPasswordEncoder().matches(request.getPassword1(), user.getPassword())).isTrue();
    }

    @Test
    void 쿠키_없이_비밀번호_변경은_오류() throws Exception {
        String url = "http://localhost:8080/users/password/new";
        PasswordUpdateRequest request = PasswordUpdateRequest.builder()
                .password1("abc12345").password2("abc12345")
                .build();

        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

}
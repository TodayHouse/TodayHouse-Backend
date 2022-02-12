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
import com.todayhouse.domain.user.dto.request.UserSignupRequest;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends IntegrationBase {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    EmailVerificationTokenRepository emailVerificationTokenRepository;

    @BeforeEach
    void clearRepository() {
        userRepository.deleteAll();
        userRepository.save(User.builder()
                .authProvider(AuthProvider.LOCAL)
                .email("test")
                .password(new BCryptPasswordEncoder().encode("12345678"))
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
        EmailVerificationToken emailToken = EmailVerificationToken.createEmailToken(email, token);
        emailToken.expireToken();
        emailVerificationTokenRepository.save(emailToken);

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 로그인() throws Exception {
        Map<String, String> user = new HashMap<>();
        user.put("email", "test");
        user.put("password", "12345678");
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

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 이메일_닉네임_중복() throws Exception {
        String url = "http://localhost:8080/users/";
        String jwt = jwtTokenProvider.createToken("test", Collections.singletonList(Role.USER));

        //email
        mockMvc.perform(MockMvcRequestBuilders.get(url + "emails/test/exist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true));
        mockMvc.perform(MockMvcRequestBuilders.get(url + "emails/fail/exist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(false));

        //nickname
        mockMvc.perform(MockMvcRequestBuilders.get(url + "nicknames/testname/exist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true));
        mockMvc.perform(MockMvcRequestBuilders.get(url + "nicknames/testfail/exist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(false));
    }
}
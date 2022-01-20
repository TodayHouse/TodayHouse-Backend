package com.todayhouse.domain.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void clearRepository() {
        userRepository.deleteAll();
        userRepository.save(User.builder()
                .email("test")
                .password(new BCryptPasswordEncoder().encode("12345678"))
                .roles(Collections.singletonList("ROLE_USER"))
                .agreePICU(true)
                .agreePromotion(true)
                .agreeTOS(true)
                .nickname("testname")
                .build());
    }

    @Test
    void 로그인() throws Exception {
        Map<String, String> user = new HashMap<>();
        user.put("email", "test");
        user.put("password", "12345678");
        String url = "http://localhost:8080/api/login";

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void jwtTest() throws Exception {
        String url = "http://localhost:8080/api/test";
        String jwt = jwtTokenProvider.createToken("test", Collections.singletonList("ROLE_USER"));

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("Authorization", "Bearer " + jwt.replace("1", "2"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    void 이메일_닉네임_중복() throws Exception {
        String url = "http://localhost:8080/api/";
        String jwt = jwtTokenProvider.createToken("test", Collections.singletonList("ROLE_USER"));

        //email
        mockMvc.perform(MockMvcRequestBuilders.get(url + "email/test/exist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true));
        mockMvc.perform(MockMvcRequestBuilders.get(url + "email/fail/exist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(false));

        //nickname
        mockMvc.perform(MockMvcRequestBuilders.get(url + "nickname/testname/exist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true));
        mockMvc.perform(MockMvcRequestBuilders.get(url + "nickname/testfail/exist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(false));
    }
}
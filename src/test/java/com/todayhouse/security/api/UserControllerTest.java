package com.todayhouse.security.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.security.config.JwtTokenProvider;
import com.todayhouse.security.domian.user.User;
import com.todayhouse.security.domian.user.UserRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    void clearRepository(){
        userRepository.deleteAll();
    }

    @Test
    void 유저등록() throws Exception{
        Map<String, String> user = new HashMap<>();
        user.put("email","test@t.com");
        user.put("password","12345");
        String url = "http://localhost:8080/api/join";

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk());

        String email = userRepository.findByEmail("test@t.com").get().getEmail();
        assertEquals(email,user.get("email"));
    }

    @Test
    void 로그인() throws Exception{
        Map<String, String> user = new HashMap<>();
        user.put("email","test@t.com");
        user.put("password","12345");
        String url = "http://localhost:8080/api/login";

        userRepository.save(User.builder()
                .email("test@t.com")
                .password(new BCryptPasswordEncoder().encode("12345"))
                .roles(Collections.singletonList("ROLE_USER"))
                .build());

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void jwtTest() throws Exception {
        userRepository.save(User.builder()
                .email("b@a.com")
                .password(new BCryptPasswordEncoder().encode("12345"))
                .roles(Collections.singletonList("ROLE_USER"))
                .build());
        String url = "http://localhost:8080/api/test";
        String jwt = jwtTokenProvider.createToken("b@a.com", Collections.singletonList("ROLE_USER"));

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("X-AUTH-TOKEN",jwt)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("X-AUTH-TOKEN",jwt.replace("1","2"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }
}
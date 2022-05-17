package com.todayhouse.global.config;

import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WebSecurityConfigTest extends IntegrationBase {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    WebSecurityConfig webSecurityConfig;

    @Test
    @DisplayName("명시하지 않은 url은 접근 금지가 default")
    void securityConfigUrlDefault() throws Exception {
        String url = "http://localhost:8080/default";
        String email = "test@test.com";
        String jwt = jwtTokenProvider.createToken(email, Collections.singletonList(Role.GUEST));
        mockMvc.perform(get(url)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
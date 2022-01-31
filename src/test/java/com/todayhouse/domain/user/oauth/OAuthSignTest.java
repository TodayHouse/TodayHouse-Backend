package com.todayhouse.domain.user.oauth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OAuthSignTest {

    @LocalServerPort
    private int port;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void OAuth_로그인_창_띄우기() throws Exception{
        String url = "http://localhost:"+port+"/oauth2/authorize/naver";
        mockMvc.perform(get(url))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", containsString("https://nid.naver.com/oauth2.0/authorize")));
    }
}

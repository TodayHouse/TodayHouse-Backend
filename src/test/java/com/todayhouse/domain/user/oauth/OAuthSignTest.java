package com.todayhouse.domain.user.oauth;

import com.todayhouse.IntegrationBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class OAuthSignTest extends IntegrationBase {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void OAuth_로그인_창_띄우기() throws Exception{
        String url = "http://localhost:8080/oauth2/authorize/naver";
        mockMvc.perform(get(url))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", containsString("https://nid.naver.com/oauth2.0/authorize")));
    }
}

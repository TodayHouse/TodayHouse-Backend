package com.todayhouse.domain.user.oauth.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.oauth.dto.OAuthAttributes;
import com.todayhouse.domain.user.oauth.dto.request.OAuthSignupRequest;
import com.todayhouse.domain.user.oauth.dto.response.OAuthSignupInfoResponse;
import com.todayhouse.domain.user.oauth.dto.response.OAuthSignupResponse;
import com.todayhouse.domain.user.oauth.dto.response.OAuthTokenResponse;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import com.todayhouse.global.config.oauth.CookieUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OAuthControllerTest extends IntegrationBase {

    @Autowired
    OAuthController oAuthController;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    void 유저정보_요청() throws Exception {
        //given
        String email = "test@test.com";
        userRepository.save(User.builder().email(email).roles(Collections.singletonList(Role.GUEST))
                .authProvider(AuthProvider.NAVER).build());
        String jwt = jwtTokenProvider.createToken(email, Collections.singletonList(Role.GUEST));
        Cookie cookie = new Cookie("auth_user", CookieUtils.serialize(jwt));
        //when
        MvcResult mvcResult = mockMvc.perform(get("http://localhost:8080/oauth2/email")
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        //then
        BaseResponse baseResponse = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), BaseResponse.class);
        OAuthSignupInfoResponse response = new ObjectMapper().convertValue(baseResponse.getResult(), OAuthSignupInfoResponse.class);
        assertThat(response.getEmail()).isEqualTo(email);
    }

    @Test
    void cookie없이_유저정보_요청() throws Exception {
        //given
        String email = "test@test.com";
        userRepository.save(User.builder().email(email).roles(Collections.singletonList(Role.GUEST))
                .authProvider(AuthProvider.NAVER).build());
        //when, then
        mockMvc.perform(get("http://localhost:8080/oauth2/signup/info"))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    void guest_cookie로_jwt_요청() throws Exception {
        //given
        String email = "test@test.com";
        userRepository.save(User.builder().email(email).roles(Collections.singletonList(Role.GUEST))
                .authProvider(AuthProvider.NAVER).nickname("test").build());
        String jwt = jwtTokenProvider.createToken(email, Collections.singletonList(Role.GUEST));
        Cookie cookie = new Cookie("auth_user", CookieUtils.serialize(jwt));
        //when,then
        mockMvc.perform(get("http://localhost:8080/oauth2/token")
                        .cookie(cookie))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @Test
    void user_cookie로_jwt_요청() throws Exception {
        //given
        String email = "test@test.com";
        userRepository.save(User.builder().email(email).roles(Collections.singletonList(Role.USER))
                .authProvider(AuthProvider.NAVER).nickname("test").build());
        String jwt = jwtTokenProvider.createToken(email, Collections.singletonList(Role.USER));
        Cookie cookie = new Cookie("auth_user", CookieUtils.serialize(jwt));
        //when
        MvcResult mvcResult = mockMvc.perform(get("http://localhost:8080/oauth2/token")
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        //then
        BaseResponse baseResponse = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), BaseResponse.class);
        OAuthTokenResponse response = new ObjectMapper().convertValue(baseResponse.getResult(), OAuthTokenResponse.class);
        assertThat(response.getAccessToken()).isEqualTo(jwt);
    }

    @Test
    void 인증없이_jwt_요청() throws Exception {
        mockMvc.perform(get("http://localhost:8080/oauth2/token"))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    @WithMockUser(roles = "USER")
    @Test
    void 인증_이메일_회원가입() throws Exception {
        //given
        String email = "test@test.com";
        String nickname = "test";
        OAuthAttributes attribute = OAuthAttributes.builder().authProvider(AuthProvider.NAVER)
                .email(email).build();
        userRepository.save(attribute.toEntity());
        OAuthSignupRequest request = OAuthSignupRequest.builder().email(email).nickname(nickname)
                .agreePICU(true).agreeAge(true).agreePromotion(true).agreeTOS(true).build();
        //when
        MvcResult mvcResult = mockMvc.perform(put("http://localhost:8080/oauth2/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andDo(print())
                .andReturn();
        //then
        BaseResponse baseResponse = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), BaseResponse.class);
        OAuthSignupResponse response = new ObjectMapper().convertValue(baseResponse.getResult(), OAuthSignupResponse.class);
        assertThat(response.getEmail()).isEqualTo(email);
        assertThat(response.getNickname()).isEqualTo(nickname);
    }

    @Test
    void 인증_안_된_이메일_회원가입() throws Exception {
        //given
        String email = "test@test.com";
        String nickname = "test";
        OAuthSignupRequest request = OAuthSignupRequest.builder().email(email).nickname(nickname)
                .agreePICU(true).agreeAge(true).agreePromotion(true).agreeTOS(true).build();
        //when,then
        mockMvc.perform(put("http://localhost:8080/oauth2/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }
}
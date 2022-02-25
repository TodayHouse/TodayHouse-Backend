package com.todayhouse.domain.user.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.dto.request.SellerRequest;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)//@BeforeAll 사용
class SellerControllerTest extends IntegrationBase {

    @Autowired
    SellerController sellerController;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @BeforeAll
    void setUp() {

    }

    @Test
    void seller_저장() throws Exception {
        String url = "http://localhost:8080/sellers";
        String userEmail = "test@test.com";
        String sellerEmail = "seller@email.com";
        userRepository.save(User.builder().email(userEmail).build());

        SellerRequest request = SellerRequest.builder()
                .companyName("a").email(sellerEmail).customerCenter("a").registrationNum(10).representative("a")
                .build();
        String jwt = tokenProvider.createToken(userEmail, Collections.singletonList(Role.USER));

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        User user = userRepository.findByEmail(userEmail).orElse(null);
        assertThat(user.getSeller().getEmail()).isEqualTo(sellerEmail);
    }

    @Test
    void 중복_seller_등록은_예외() throws Exception {
        String url = "http://localhost:8080/sellers";
        String userEmail = "test@test.com";
        String sellerEmail = "seller@email.com";
        Seller seller = Seller.builder().email(sellerEmail).build();
        userRepository.save(User.builder().email(userEmail).seller(seller).build());

        SellerRequest request = SellerRequest.builder()
                .companyName("a").email(sellerEmail).customerCenter("a").registrationNum(10).representative("a")
                .build();
        String jwt = tokenProvider.createToken(userEmail, Collections.singletonList(Role.USER));

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void jwt_없이_등록은_예외() throws Exception {
        String url = "http://localhost:8080/sellers";
        String userEmail = "test@test.com";
        String sellerEmail = "seller@email.com";
        userRepository.save(User.builder().email(userEmail).build());

        SellerRequest request = SellerRequest.builder()
                .companyName("a").email(sellerEmail).customerCenter("a").registrationNum(10).representative("a")
                .build();

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void seller_찾았다() throws Exception {
        String url = "http://localhost:8080/sellers/";
        String userEmail = "test@test.com";
        String sellerEmail = "seller@email.com";
        Seller seller = Seller.builder().email(sellerEmail).build();
        User user = userRepository.save(User.builder().email(userEmail).seller(seller).build());

        MvcResult mvcResult = mockMvc.perform(get(url + user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        BaseResponse baseResponse = objectMapper.readValue(contentAsString, BaseResponse.class);
        Seller result = objectMapper.readValue(objectMapper.writeValueAsString(baseResponse.getResult()), new TypeReference<>() {
        });
        assertThat(result).usingRecursiveComparison().isEqualTo(seller);
    }

    @Test
    void seller_못찾았다() throws Exception {
        String url = "http://localhost:8080/sellers/";
        String userEmail = "test@test.com";
        User user = userRepository.save(User.builder().email(userEmail).build());

        MvcResult mvcResult = mockMvc.perform(get(url + user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        BaseResponse baseResponse = objectMapper.readValue(contentAsString, BaseResponse.class);
        Seller result = objectMapper.readValue(objectMapper.writeValueAsString(baseResponse.getResult()), new TypeReference<>() {
        });
        assertThat(result).isNull();
    }
}

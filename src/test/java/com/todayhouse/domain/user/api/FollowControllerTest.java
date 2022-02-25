package com.todayhouse.domain.user.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.user.dao.FollowRepository;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Follow;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.dto.SimpleUser;
import com.todayhouse.domain.user.dto.request.FollowRequest;
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
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)//@BeforeAll 사용
class FollowControllerTest extends IntegrationBase {

    @Autowired
    FollowController followController;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeAll
    void preSet() {
        User user1 = User.builder().email("user1@test").build();
        User user2 = User.builder().email("user2@test").build();
        User user3 = User.builder().email("user3@test").build();
        User user4 = User.builder().email("user4@test").build();
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        followRepository.save(Follow.builder().from(user1).to(user2).build());
        followRepository.save(Follow.builder().from(user1).to(user3).build());
        followRepository.save(Follow.builder().from(user1).to(user4).build());
    }

    @Test
    void 팔로우_관계_저장() throws Exception {
        String url = "http://localhost:8080/follows";
        Long user2Id = userRepository.findByEmail("user2@test").orElse(null).getId();
        Long user3Id = userRepository.findByEmail("user3@test").orElse(null).getId();
        FollowRequest request = FollowRequest.builder().fromId(user2Id).toId(user3Id).build();// user2, user3
        String jwt = tokenProvider.createToken("user2@test", Collections.singletonList(Role.USER));

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        List<Follow> list = followRepository.findAll();
        assertThat(list.stream().anyMatch(follow ->
                follow.getFrom().getId() == user2Id && follow.getTo().getId() == user3Id)).isTrue();
    }

    @Test
    void 팔로우_끊기() throws Exception {
        String url = "http://localhost:8080/follows";
        Long user1Id = userRepository.findByEmail("user1@test").orElse(null).getId();
        Long user2Id = userRepository.findByEmail("user2@test").orElse(null).getId();
        FollowRequest request = FollowRequest.builder().fromId(user1Id).toId(user2Id).build();// user1, user2
        String jwt = tokenProvider.createToken("user1@test", Collections.singletonList(Role.USER));

        mockMvc.perform(delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        List<Follow> list = followRepository.findAll();
        assertThat(list.stream().anyMatch(follow ->
                follow.getFrom().getId() == user1Id && follow.getTo().getId() == user2Id)).isFalse();
        assertThat(userRepository.existsById(user1Id)).isTrue();
        assertThat(userRepository.existsById(user2Id)).isTrue();
    }

    @Test
    void 팔로워_수_구하기() throws Exception {
        // user2 팔로워 1명
        Long id = userRepository.findByEmail("user2@test").orElse(null).getId();
        mockMvc.perform(get("http://localhost:8080/follows/followers/count/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(1));
    }

    @Test
    void 팔로잉_수_구하기() throws Exception {
        // user1 팔로잉 3명
        Long id = userRepository.findByEmail("user1@test").orElse(null).getId();
        mockMvc.perform(get("http://localhost:8080/follows/followings/count/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(3));
    }

    @Test
    void 팔로워_리스트() throws Exception {
        // user2 팔로워 1명
        Long user1Id = userRepository.findByEmail("user1@test").orElse(null).getId();
        Long user2Id = userRepository.findByEmail("user2@test").orElse(null).getId();
        MvcResult mvcResult = mockMvc.perform(get("http://localhost:8080/follows/followers/" + user2Id))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        BaseResponse baseResponse = objectMapper.readValue(contentAsString, BaseResponse.class);
        Set<SimpleUser> result = objectMapper.readValue(objectMapper.writeValueAsString(baseResponse.getResult()), new TypeReference<>() {
        });
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).extracting("id", Long.class).contains(user1Id);
    }

    @Test
    void 팔로잉_리스트() throws Exception {
        // user1 팔로잉 3명
        Long id = userRepository.findByEmail("user1@test").orElse(null).getId();
        Long user2Id = userRepository.findByEmail("user2@test").orElse(null).getId();
        Long user3Id = userRepository.findByEmail("user3@test").orElse(null).getId();
        Long user4Id = userRepository.findByEmail("user4@test").orElse(null).getId();
        MvcResult mvcResult = mockMvc.perform(get("http://localhost:8080/follows/followings/" + id))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        BaseResponse baseResponse = objectMapper.readValue(contentAsString, BaseResponse.class);
        Set<SimpleUser> result = objectMapper.readValue(objectMapper.writeValueAsString(baseResponse.getResult()), new TypeReference<>() {
        });
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).extracting("id", Long.class).contains(user2Id, user3Id, user4Id);
    }

    @Test
    void jwt_subject와_다른_follow_설정은_오류() throws Exception {
        // user3이 user2->user4 팔로우 설정
        String url = "http://localhost:8080/follows";
        Long user2Id = userRepository.findByEmail("user2@test").orElse(null).getId();
        Long user4Id = userRepository.findByEmail("user4@test").orElse(null).getId();
        FollowRequest request = FollowRequest.builder().fromId(user2Id).toId(user4Id).build();// user1, user2
        String jwt = tokenProvider.createToken("user3@test", Collections.singletonList(Role.USER));

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void 팔로우_여부() throws Exception {
        int id1 = Math.toIntExact(userRepository.findByEmail("user1@test").orElse(null).getId());
        int id2 = Math.toIntExact(userRepository.findByEmail("user2@test").orElse(null).getId());
        int id3 = Math.toIntExact(userRepository.findByEmail("user3@test").orElse(null).getId());
        String url = "http://localhost:8080/follows?";

        MvcResult mvcResult1 = mockMvc.perform(get(url + "fromId=" + id1 + "&toId=" + id2)).andExpect(status().isOk()).andReturn();
        MvcResult mvcResult2 = mockMvc.perform(get(url + "fromId=" + id2 + "&toId=" + id3)).andExpect(status().isOk()).andReturn();

        String contentAsString1 = mvcResult1.getResponse().getContentAsString();
        BaseResponse baseResponse1 = objectMapper.readValue(contentAsString1, BaseResponse.class);

        String contentAsString2 = mvcResult2.getResponse().getContentAsString();
        BaseResponse baseResponse2 = objectMapper.readValue(contentAsString2, BaseResponse.class);

        assertThat(baseResponse1.getResult()).isEqualTo(true);
        assertThat(baseResponse2.getResult()).isEqualTo(false);
    }
}
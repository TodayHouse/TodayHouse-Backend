package com.todayhouse.domain.story.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.story.dao.StoryRepository;
import com.todayhouse.domain.story.domain.FamilyType;
import com.todayhouse.domain.story.domain.ResiType;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.domain.StyleType;
import com.todayhouse.domain.story.dto.reqeust.ReplyCreateRequest;
import com.todayhouse.domain.story.dto.reqeust.ReplyDeleteRequest;
import com.todayhouse.domain.story.dto.reqeust.StoryCreateRequest;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StoryControllerTest extends IntegrationBase {
    @Autowired
    UserRepository userRepository;
    @Autowired
    StoryRepository storyRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenProvider provider;

    String jwt = null;
    String storyUrl = "http://localhost:8080/stories/";
    User user;

    @BeforeEach
    void setup() {
        Seller seller = Seller.builder().brand("test_brand").companyName("test").build();

        user = userRepository.save(User.builder()
                .authProvider(AuthProvider.LOCAL)
                .email("admin@test.com")
                .roles(Collections.singletonList(Role.ADMIN))
                .nickname("admin")
                .seller(seller)
                .build());

        jwt = provider.createToken("admin@test.com", Collections.singletonList(Role.USER));
        Story s1 = Story.builder().liked(1).title("제목").content("내용").category(Story.Category.STORY).user(user).build();
        storyRepository.save(s1);

    }

    @Test
    @DisplayName("스토리 생성")
    void createStory() throws Exception {
        String url = "http://localhost:8080/stories/";
        MockMultipartFile multipartFile = new MockMultipartFile("file", "foo.jpg", "image/jpeg", "테스트".getBytes(StandardCharsets.UTF_8));


        StoryCreateRequest storyCreateRequest = StoryCreateRequest.builder().title("제목").content("내용").category(Story.Category.STORY).build();
        MockMultipartFile request = new MockMultipartFile("request", "json", "application/json", objectMapper.writeValueAsBytes(storyCreateRequest));
        mockMvc.perform(
                multipart(url)
                        .file(multipartFile)
                        .file(request)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization", "Bearer " + jwt)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글 생성")
    void createReply() throws Exception {
        Story story = storyRepository.findAll().get(0);
        String url = storyUrl + "reply";

        ReplyCreateRequest request = new ReplyCreateRequest("댓글입니다", story.getId());
        mockMvc.perform(post(url)
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글 삭제")
    void deleteReply() throws Exception {
        String url = storyUrl + "reply";
        ReplyDeleteRequest replyDeleteRequest = new ReplyDeleteRequest(1L);
        mockMvc.perform(delete(url)
                .header("Authorization", "Bearer " + jwt)
                .content(objectMapper.writeValueAsString(replyDeleteRequest))
                .contentType("application/json")
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글 페이지 조회")
    void findReplies() throws Exception {
        String url = storyUrl + "reply";
        MvcResult mvcResult = mockMvc.perform(get(url)
                .param("storyId", "1")
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String contentAsString = response.getContentAsString();
        System.out.println("contentAsString = " + contentAsString);
    }

    @Test
    @DisplayName("스토리 필터링 조회")
    void searchStory() throws Exception {
        String url = storyUrl;

        FamilyType[] familyTypes = FamilyType.values();
        StyleType[] styleTypes = StyleType.values();
        ResiType[] resiTypes = ResiType.values();
        Optional<User> byId = userRepository.findById(user.getId());
        Story.Category[] categories = Story.Category.values();
        int likes = 0;
        for (Story.Category category : categories) {
            for (ResiType resiType : resiTypes) {
                for (StyleType styleType : styleTypes) {
                    for (FamilyType familyType : familyTypes) {
                        for (int floorSpace = 0; floorSpace < 5; floorSpace++) {
                            Story build = Story.builder()
                                    .styleType(styleType)
                                    .category(Story.Category.STORY)
                                    .floorSpace(floorSpace)
                                    .resiType(resiType)
                                    .familyType(familyType)
                                    .content("내용")
                                    .title("제목")
                                    .liked(likes++)
                                    .user(byId.orElseThrow())
                                    .build();
                            storyRepository.save(build);
                        }
                    }
                }
            }

        }

        MvcResult mvcResult = mockMvc.perform(get(url)
                .param("floorSpace", "3")
                .header("Authorization", "Bearer " + jwt)
                .contentType("application/json")
        ).andReturn();

    }

}
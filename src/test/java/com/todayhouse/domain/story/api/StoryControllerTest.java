package com.todayhouse.domain.story.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.scrap.dao.ScrapRepository;
import com.todayhouse.domain.scrap.domain.Scrap;
import com.todayhouse.domain.story.dao.StoryReplyRepository;
import com.todayhouse.domain.story.dao.StoryRepository;
import com.todayhouse.domain.story.domain.*;
import com.todayhouse.domain.story.dto.reqeust.ReplyCreateRequest;
import com.todayhouse.domain.story.dto.reqeust.ReplyDeleteRequest;
import com.todayhouse.domain.story.dto.reqeust.StoryCreateRequest;
import com.todayhouse.domain.story.dto.response.StoryGetDetailResponse;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StoryControllerTest extends IntegrationBase {
    @Autowired
    UserRepository userRepository;
    @Autowired
    StoryRepository storyRepository;
    @Autowired
    ScrapRepository scrapRepository;

    @Autowired
    StoryReplyRepository replyRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenProvider provider;

    String jwt = null;
    String storyUrl = "http://localhost:8080/stories/";
    User user;
    Story s1;
    StoryReply r1;
    StoryReply r2;
    StoryReply r3;

    @BeforeEach
    void setup() {
        Seller seller = Seller.builder().brand("test_brand").companyName("test").build();

        user = userRepository.save(User.builder()
                .authProvider(AuthProvider.LOCAL)
                .email("admin@test.com")
                .roles(Collections.singletonList(Role.ADMIN))
                .nickname("admin1")
                .seller(seller)
                .build());

        jwt = provider.createToken("admin@test.com", Collections.singletonList(Role.USER));
        s1 = Story.builder().liked(1).title("제목").content("내용").category(Story.Category.STORY).user(user).build();
        s1 = storyRepository.save(s1);
        r1 = StoryReply.builder().story(s1).user(user).content("r1").build();
        r2 = StoryReply.builder().story(s1).user(user).content("r2").build();
        r3 = StoryReply.builder().story(s1).user(user).content("r3").build();
        replyRepository.save(r1);
        replyRepository.save(r2);
        replyRepository.save(r3);
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
        StoryReply storyReply = replyRepository.findAll().get(2);
        String url = storyUrl + "reply/" + storyReply.getId();
        mockMvc.perform(delete(url)
                .header("Authorization", "Bearer " + jwt)
                .contentType("application/json")
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글 페이지 조회")
    void findReplies() throws Exception {
        String url = storyUrl + "reply/1";
        MvcResult mvcResult = mockMvc.perform(get(url)
                .contentType("application/json")
                .header("Authorization", "Bearer " + jwt)).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String contentAsString = response.getContentAsString();
        System.out.println("contentAsString = " + contentAsString);
    }

    @Test
    @DisplayName("스토리 id 조회")
    void findById() throws Exception {
        String url = storyUrl + "1";
        MvcResult mvcResult = mockMvc.perform(get(url)
                .contentType("applicaation/json")
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

    @Test
    @DisplayName("스토리 조회 시 스크랩 유무 포함")
    void findAllDescWithScrap() throws Exception {
        Story s2 = Story.builder().liked(1).title("제목2").content("내용").category(Story.Category.STORY).user(user).build();
        s2 = storyRepository.save(s2);
        scrapRepository.save(Scrap.builder().user(user).story(s2).build());

        mockMvc.perform(get("http://localhost:8080/stories/" + s1.getId()))
                .andExpect(status().isOk()); // 조회수 증가

        mockMvc.perform(get("http://localhost:8080/stories?sort=id,DESC")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content", hasSize(2)))
                .andExpect(jsonPath("$.result.content[0].isScraped", equalTo(true)))
                .andExpect(jsonPath("$.result.content[0].title", equalTo("제목2")))
                .andExpect(jsonPath("$.result.content[0].views", equalTo(0)))
                .andExpect(jsonPath("$.result.content[1].isScraped", equalTo(false)))
                .andExpect(jsonPath("$.result.content[1].title", equalTo("제목")))
                .andExpect(jsonPath("$.result.content[1].views", equalTo(1)))
                .andDo(print());
    }

    @Test
    @DisplayName("스토리 조회 시 view 증가")
    void increaseView() throws Exception {
        mockMvc.perform(get("http://localhost:8080/stories/" + s1.getId()))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(get("http://localhost:8080/stories/" + s1.getId()))
                .andExpect(status().isOk())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        StoryGetDetailResponse story = objectMapper.registerModule(new JavaTimeModule()).convertValue(response.getResult(), StoryGetDetailResponse.class);
        assertThat(story.getViews()).isEqualTo(2);
    }
}
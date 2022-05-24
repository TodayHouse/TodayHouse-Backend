package com.todayhouse.domain.story.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.domain.story.dao.StoryRepository;
import com.todayhouse.domain.story.domain.FamilyType;
import com.todayhouse.domain.story.domain.ResiType;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.domain.StyleType;
import com.todayhouse.domain.story.dto.reqeust.CreateReplyRequest;
import com.todayhouse.domain.story.dto.reqeust.DeleteReplyRequest;
import com.todayhouse.domain.story.dto.reqeust.StoryCreateRequest;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@Rollback(value = false)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StoryControllerTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    StoryRepository storyRepository;
    @PersistenceContext
    EntityManager entityManager;
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenProvider provider;

    String jwt = null;
    String storyUrl = "http://localhost:8080/stories/";

    @BeforeEach
    void setup() throws Exception {
        String url = "http://localhost:8080/users/test";
        jwt = provider.createToken("admin@admin.com", Collections.singletonList(Role.USER));
        Cookie cookie = new Cookie();
    }

    @AfterEach
    void after() {

        entityManager.flush();
        entityManager.clear();

    }

    @Test
    @DisplayName("스토리 생성")
    @Order(1)
    void createStory() throws Exception {
        String url = "http://localhost:8080/stories/";
        MockMultipartFile multipartFile = new MockMultipartFile("file", "foo.jpg", "image/jpeg", "테스트".getBytes(StandardCharsets.UTF_8));


        StoryCreateRequest storyCreateRequest = StoryCreateRequest.builder().build();
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
    @Order(2)
    void createReply() throws Exception {
        Story story = storyRepository.findAll().get(0);
        String url = storyUrl + "reply";

        CreateReplyRequest request = new CreateReplyRequest("댓글입니다", story.getId());
        mockMvc.perform(post(url)
                        .header("Authorization", "Bearer " + jwt)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글 삭제")
    @Order(3)
    void deleteReply() throws Exception {
        String url = storyUrl + "reply";
        DeleteReplyRequest deleteReplyRequest = new DeleteReplyRequest(1L);
        mockMvc.perform(delete(url)
                .header("Authorization", "Bearer " + jwt)
                .content(objectMapper.writeValueAsString(deleteReplyRequest))
                .contentType("application/json")
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글 페이지 조회")
    @Order(4)
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
    @Order(5)
    void searchStory() throws Exception {
        String url = storyUrl;

        List<FamilyType> familyTypes = Arrays.asList(FamilyType.values());
        List<StyleType> styleTypes = Arrays.asList(StyleType.values());
        List<ResiType> resiTypes = Arrays.asList(ResiType.values());
        Optional<User> byId = userRepository.findById(1L);

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
                                .liked(0)
                                .user(byId.get())
                                .build();
                        storyRepository.save(build);

                    }
                }
            }
        }

        MvcResult mvcResult = mockMvc.perform(get(url)
                .param("floorSpace", "3")
                .header("Authorization", "Bearer " + jwt)
                .contentType("application/json")
        ).andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String contentAsString = response.getContentAsString();
        System.out.println("response = " + contentAsString);

    }

}
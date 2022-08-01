package com.todayhouse.domain.likes.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.likes.dao.LikesStoryRepository;
import com.todayhouse.domain.likes.domain.LikesStory;
import com.todayhouse.domain.likes.domain.LikesType;
import com.todayhouse.domain.likes.dto.LikesRequest;
import com.todayhouse.domain.likes.dto.UnLikesRequest;
import com.todayhouse.domain.story.dao.StoryRepository;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.domain.StoryReply;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LikesStoryControllerTest extends IntegrationBase {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    StoryRepository storyRepository;
    @Autowired
    LikesStoryRepository likesStoryRepository;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenProvider provider;

    User user;
    String jwt;

    String url = "http://localhost:8080/likes";

    Story s1;
    StoryReply sr1;

    private Story s2;
    Seller seller1;
    @PersistenceContext
    EntityManager em;
    StoryReply sr2;
    User user2;

    @BeforeEach
    void setup() {
        seller1 = Seller.builder().email("seller1@email.com").brand("user1").build();
        user = userRepository.save(User.builder()
                .authProvider(AuthProvider.LOCAL)
                .email("admin@test.com")
                .roles(Collections.singletonList(Role.ADMIN))
                .seller(seller1)
                .nickname("admin1")
                .build());
        user2 = userRepository.save(User.builder()
                .authProvider(AuthProvider.LOCAL)
                .email("admin2@test.com")
                .roles(Collections.singletonList(Role.ADMIN))
                .seller(seller1)
                .nickname("admin2")
                .build());

        jwt = provider.createToken("admin@test.com", Collections.singletonList(Role.USER));

        s1 = Story.builder()
                .content("내용")
                .user(user)
                .category(Story.Category.STORY)
                .title("제목")
                .build();
        s2 = Story.builder()
                .content("내용")
                .user(user)
                .category(Story.Category.STORY)
                .title("제목")
                .build();
        sr1 = StoryReply.builder().story(s1)
                .content("댓글 내용")
                .user(user)
                .build();
        em.persist(sr1);
        s1.getStoryReplies().add(sr1);

        sr2 = StoryReply.builder().story(s1)
                .content("댓글 내용")
                .user(user)
                .build();
        em.persist(sr2);
        s1.getStoryReplies().add(sr2);

        storyRepository.save(s1);
        storyRepository.save(s2);

        LikesStory ls1 = new LikesStory(user, s1);
        s1.getLikesStories().add(ls1);
        likesStoryRepository.save(ls1);

        LikesStory ls2 = new LikesStory(user2, s1);
        s1.getLikesStories().add(ls2);
        likesStoryRepository.save(ls2);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("스토리 좋아요 테스트")
    void likes() throws Exception {
        LikesRequest likesRequest = new LikesRequest(LikesType.STORY, s2.getId());
        mockMvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(likesRequest))
                        .header("Authorization", "Bearer " + jwt)

                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.likesCount").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("스토리 좋아요 삭제")
    void deleteLikes() throws Exception {

        UnLikesRequest unLikesRequest = new UnLikesRequest(LikesType.STORY, s1.getId());

        mockMvc.perform(delete(url)
                        .content(objectMapper.writeValueAsString(unLikesRequest))
                        .header("Authorization", "Bearer " + jwt)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.liked").value(false));
    }

    @Test
    @DisplayName("스토리 조회시 좋아요 확인")
    public void checkStoryIsLiked() throws Exception {
        Long id = s1.getId();
        mockMvc.perform(get("http://localhost:8080/stories/" + id)
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + jwt))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.liked").value(true))
                .andExpect(jsonPath("$.result.likesCount").value(2));
    }

    @Test
    @DisplayName("스토리 이미 좋아요 검증")
    void alreadyLikes() throws Exception {
        LikesRequest likesRequest = new LikesRequest(LikesType.STORY, s1.getId());
        mockMvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(likesRequest))
                        .header("Authorization", "Bearer " + jwt)

                        .contentType("application/json"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.code").value(3500))
                .andDo(print());
    }
}

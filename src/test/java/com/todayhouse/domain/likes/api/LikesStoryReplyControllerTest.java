package com.todayhouse.domain.likes.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.likes.domain.LikesStoryReply;
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
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LikesStoryReplyControllerTest extends IntegrationBase {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    StoryRepository storyRepository;

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
    StoryReply sr2;

    Seller seller1;
    @PersistenceContext
    EntityManager em;


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

        jwt = provider.createToken("admin@test.com", Collections.singletonList(Role.USER));

        s1 = Story.builder()
                .content("내용")
                .user(user)
                .category(Story.Category.STORY)
                .title("제목")
                .build();
        Story s2 = Story.builder()
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

        LikesStoryReply likesStoryReply = new LikesStoryReply(user, sr2);
        em.persist(likesStoryReply);
        sr2.getLikesStoryReplies().add(likesStoryReply);


    }

    @Test
    @DisplayName("댓글 좋아요")
    public void likesReply() throws Exception {
        LikesRequest request = new LikesRequest(LikesType.STORY_REPLY, sr1.getId());
        mockMvc.perform(post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + jwt))
                .andDo(print())
                .andExpect(jsonPath("$.result.liked").value(true))
                .andExpect(jsonPath("$.result.likesCount").value(1));

    }

    @Test
    @DisplayName("댓글 좋아요 삭제")
    public void deleteLikesReply() throws Exception {

        UnLikesRequest unLikesRequest = new UnLikesRequest(LikesType.STORY_REPLY, sr2.getId());
        mockMvc.perform(delete(url)
                        .content(objectMapper.writeValueAsString(unLikesRequest))
                        .header("Authorization", "Bearer " + jwt)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.liked").value(false))
                .andExpect(jsonPath("$.result.likesCount").value(0));

    }

    @Test
    @DisplayName("댓글 조회시 좋아요 확인")
    public void checkReply() throws Exception {
        mockMvc.perform(get("http://localhost:8080/stories/reply/"+ s1.getId())
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + jwt)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content.[1].liked").value(true));


    }

    @Test
    @DisplayName("게스트 계정 조회시 좋아요 확인")
    public void checkGuestReply() throws Exception {
        mockMvc.perform(get("http://localhost:8080/stories/reply/"+ s1.getId())
                        .contentType("application/json")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content.[1].liked").value(false));

    }


}

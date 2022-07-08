package com.todayhouse.domain.scrap.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.scrap.dao.ScrapRepository;
import com.todayhouse.domain.scrap.domain.Scrap;
import com.todayhouse.domain.story.dao.StoryRepository;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.dto.response.StoryGetListResponse;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.global.common.PageDto;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ScrapControllerTest extends IntegrationBase {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ScrapRepository scrapRepository;
    @Autowired
    StoryRepository storyRepository;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    User user1;
    Story story1;

    @BeforeEach
    void preSet() {
        user1 = userRepository.save(User.builder().build());
        story1 = storyRepository.save(Story.builder()
                .category(Story.Category.STORY).content("content").title("title").liked(0).user(user1)
                .build());
    }

    @Test
    @DisplayName("스크랩 저장")
    void saveScrap() throws Exception {
        String url = "http://localhost:8080/scraps/" + story1.getId();
        String jwt = jwtTokenProvider.createToken(user1.getEmail(), List.of(Role.USER));

        MvcResult mvcResult = mockMvc.perform(post(url)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        Long saveId = objectMapper.convertValue(response.getResult(), Long.class);
        Scrap scrap = scrapRepository.findByUserAndStory(user1, story1).orElse(null);

        assertThat(saveId).isEqualTo(scrap.getId());
    }

    @Test
    @DisplayName("스크랩 되었는지 확인")
    void isScraped() throws Exception {
        scrapRepository.save(Scrap.builder().story(story1).user(user1).build());
        String url = "http://localhost:8080/scraps/" + story1.getId() + "/exist";
        String jwt = jwtTokenProvider.createToken(user1.getEmail(), List.of(Role.USER));

        MvcResult mvcResult = mockMvc.perform(get(url)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        Boolean flag = objectMapper.convertValue(response.getResult(), Boolean.class);

        assertTrue(flag);
    }

    @Test
    @DisplayName("스크랩 삭제")
    void deleteScrap() throws Exception {
        Scrap saveScrap = scrapRepository.save(Scrap.builder().story(story1).user(user1).build());
        String url = "http://localhost:8080/scraps/" + story1.getId();
        String jwt = jwtTokenProvider.createToken(user1.getEmail(), List.of(Role.USER));

        mockMvc.perform(delete(url)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andDo(print());

        Scrap findScrap = scrapRepository.findById(saveScrap.getId()).orElse(null);
        assertNull(findScrap);
    }

    @Test
    @DisplayName("해당 상품의 총 스크랩 개수")
    void countStoryScrap() throws Exception {
        scrapRepository.save(Scrap.builder().user(user1).story(story1).build());
        scrapRepository.save(Scrap.builder().user(user1).story(story1).build());

        String url = "http://localhost:8080/scraps/" + story1.getId() + "/count";

        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        Long count = objectMapper.convertValue(response.getResult(), Long.class);

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("자신이 스크랩한 사진의 개수")
    void countMyScrap() throws Exception {
        scrapRepository.save(Scrap.builder().user(user1).story(story1).build());
        scrapRepository.save(Scrap.builder().user(user1).story(story1).build());

        String url = "http://localhost:8080/scraps/my/count";
        String jwt = jwtTokenProvider.createToken(user1.getEmail(), List.of(Role.USER));

        MvcResult mvcResult = mockMvc.perform(get(url)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        Long count = objectMapper.convertValue(response.getResult(), Long.class);

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("스크랩한 스토리 날짜 내림차순 조회")
    void findScrapedStories() throws Exception {
        Story story2 = storyRepository.save(Story.builder()
                .category(Story.Category.STORY).content("content2").title("title2").liked(0).user(user1)
                .build());
        scrapRepository.save(Scrap.builder().user(user1).story(story1).build());
        scrapRepository.save(Scrap.builder().user(user1).story(story2).build());

        String url = "http://localhost:8080/scraps/my?sort=createdAt,DESC";
        String jwt = jwtTokenProvider.createToken(user1.getEmail(), List.of(Role.USER));

        MvcResult mvcResult = mockMvc.perform(get(url)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        PageDto<StoryGetListResponse> pageDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getResult()), new TypeReference<>() {
        });
        List<StoryGetListResponse> stories = objectMapper.readValue(objectMapper.writeValueAsString(pageDto.getContent()), new TypeReference<>() {
        });

        assertThat(pageDto.getTotalElements()).isEqualTo(2);
        assertThat(stories.get(0).getTitle()).isEqualTo("title2");
        assertThat(stories.get(1).getTitle()).isEqualTo("title");
    }
}
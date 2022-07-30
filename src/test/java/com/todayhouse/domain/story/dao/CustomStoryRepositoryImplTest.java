package com.todayhouse.domain.story.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.dto.reqeust.StorySearchRequest;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CustomStoryRepositoryImplTest extends DataJpaBase {
    @Autowired
    StoryRepository storyRepository;
    @Autowired
    UserRepository userRepository;

    User user1, user2;
    Story s1, s2, s3;

    @BeforeEach
    void setUp() {
        Seller seller = Seller.builder().brand("test_brand").companyName("test").build();

        user1 = userRepository.save(User.builder()
                .authProvider(AuthProvider.LOCAL)
                .email("admin@test.com")
                .roles(Collections.singletonList(Role.ADMIN))
                .nickname("admintest")
                .seller(seller)
                .build());

        user2 = userRepository.save(User.builder()
                .authProvider(AuthProvider.LOCAL)
                .email("user1@test.com")
                .roles(Collections.singletonList(Role.ADMIN))
                .nickname("usertest1")
                .seller(seller)
                .build());

        s1 = storyRepository.save(Story.builder().title("제목1").content("내용1").category(Story.Category.STORY).user(user1).build());
        s2 = storyRepository.save(Story.builder().title("제목2").content("내용2").category(Story.Category.STORY).user(user1).build());
        s3 = storyRepository.save(Story.builder().title("제목3").content("내용3").category(Story.Category.STORY).user(user2).build());
    }

    @Test
    @DisplayName("스토리 작성자 nickname으로 검색")
    void searchConditionSearchNickName() {
        StorySearchRequest storySearchRequest = StorySearchRequest.builder().search("admintes").build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Story> stories = storyRepository.searchCondition(storySearchRequest, pageRequest).getContent();

        assertThat(stories).hasSize(2);
        assertThat(stories.get(0)).isEqualTo(s1);
        assertThat(stories.get(1)).isEqualTo(s2);
    }

    @Test
    @DisplayName("스토리 제목으로 검색")
    void searchConditionSearchTitle() {
        StorySearchRequest storySearchRequest1 = StorySearchRequest.builder().search("제목").build();
        StorySearchRequest storySearchRequest2 = StorySearchRequest.builder().search("제목2").build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Story> storiesAll = storyRepository.searchCondition(storySearchRequest1, pageRequest).getContent();
        List<Story> stories2 = storyRepository.searchCondition(storySearchRequest2, pageRequest).getContent();

        assertThat(storiesAll).hasSize(3);
        assertThat(storiesAll.get(0)).isEqualTo(s1);
        assertThat(storiesAll.get(1)).isEqualTo(s2);
        assertThat(storiesAll.get(2)).isEqualTo(s3);

        assertThat(stories2).hasSize(1);
        assertThat(stories2.get(0)).isEqualTo(s2);
    }

    @Test
    @DisplayName("스토리 내용으로 검색")
    void searchConditionSearchContent() {
        StorySearchRequest storySearchRequest = StorySearchRequest.builder().search("제목2").build();
        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Story> stories = storyRepository.searchCondition(storySearchRequest, pageRequest).getContent();

        assertThat(stories).hasSize(1);
        assertThat(stories.get(0)).isEqualTo(s2);
    }
}
package com.todayhouse.domain.story.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.scrap.dao.ScrapRepository;
import com.todayhouse.domain.scrap.domain.Scrap;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StoryRepositoryTest extends DataJpaBase {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ScrapRepository scrapRepository;
    @Autowired
    StoryRepository storyRepository;

    User user1;
    Story story1, story2, story3;
    @BeforeEach
    void preSet(){
        user1 = userRepository.save(User.builder().email("test").build());
        story1 = storyRepository.save(Story.builder()
                .category(Story.Category.STORY).content("content1").title("title1").liked(0).user(user1)
                .build());
        story2 = storyRepository.save(Story.builder()
                .category(Story.Category.STORY).content("content2").title("title3").liked(0).user(user1)
                .build());
        story3 = storyRepository.save(Story.builder()
                .category(Story.Category.STORY).content("content2").title("title3").liked(0).user(user1)
                .build());
    }

    @Test
    void findScrapedByStoriesAndUser() {
        scrapRepository.save(Scrap.builder().story(story2).user(user1).build());
        scrapRepository.save(Scrap.builder().story(story3).user(user1).build());
        List<Story> stories = List.of(story1, story2, story3);

        List<Story> scrapedStories = storyRepository.findScrapedByStoriesAndUser(stories, user1);

        assertThat(scrapedStories).hasSize(2);
        assertThat(scrapedStories).contains(story2);
        assertThat(scrapedStories).contains(story3);
    }
}
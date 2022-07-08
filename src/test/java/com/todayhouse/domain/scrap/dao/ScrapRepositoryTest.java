package com.todayhouse.domain.scrap.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.scrap.domain.Scrap;
import com.todayhouse.domain.story.dao.StoryRepository;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

class ScrapRepositoryTest extends DataJpaBase {
    @Autowired
    StoryRepository storyRepository;
    @Autowired
    ScrapRepository scrapRepository;
    @Autowired
    UserRepository userRepository;

    User user1, user2;
    Story story1, story2;
    Scrap scrap1, scrap2, scrap3;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(User.builder().build());
        user2 = userRepository.save(User.builder().build());

        story1 = storyRepository.save(Story.builder()
                .category(Story.Category.STORY).content("content").title("title1").liked(0).user(user1)
                .build());
        story2 = storyRepository.save(Story.builder()
                .category(Story.Category.STORY).content("content").title("title2").liked(0).user(user2)
                .build());

        scrap1 = scrapRepository.save(Scrap.builder().user(user1).story(story1).build());
        scrap2 = scrapRepository.save(Scrap.builder().user(user1).story(story2).build());
        scrap3 = scrapRepository.save(Scrap.builder().user(user2).story(story1).build());
    }

    @Test
    @DisplayName("유저와 상품으로 스크랩 찾기")
    void findByUserAndStory() {
        Scrap findScrap1 = scrapRepository.findByUserAndStory(user1, story1).orElse(null);
        Scrap findScrap2 = scrapRepository.findByUserAndStory(user1, story2).orElse(null);
        Scrap findScrap3 = scrapRepository.findByUserAndStory(user2, story1).orElse(null);

        assertThat(findScrap1).isEqualTo(scrap1);
        assertThat(findScrap2).isEqualTo(scrap2);
        assertThat(findScrap3).isEqualTo(scrap3);
    }

    @Test
    @DisplayName("상품 id로 스크랩 개수 세기")
    void countByStoryId() {
        Story tmp = storyRepository.save(Story.builder()
                .category(Story.Category.STORY).content("content").title("title").liked(0).user(user1)
                .build());

        Long count1 = scrapRepository.countByStory(story1);
        Long count2 = scrapRepository.countByStory(story2);
        Long count3 = scrapRepository.countByStory(tmp);

        assertThat(count1).isEqualTo(2);
        assertThat(count2).isEqualTo(1);
        assertThat(count3).isEqualTo(0);
    }

    @Test
    @DisplayName("유저로 스크랩 개수 세기")
    void countByUser() {
        User tmp = userRepository.save(User.builder().build());

        Long count1 = scrapRepository.countByUser(user1);
        Long count2 = scrapRepository.countByUser(user2);
        Long count3 = scrapRepository.countByUser(tmp);

        assertThat(count1).isEqualTo(2);
        assertThat(count2).isEqualTo(1);
        assertThat(count3).isEqualTo(0);
    }

    @Test
    @DisplayName("스크랩한 스토리만 페이징")
    void findScrapWithStoryByUser() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Scrap> scraps = scrapRepository.findScrapWithStoryByUser(pageRequest, user1);

        assertThat(scraps.getContent().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("스크랩한 스토리만 페이징 결과 0")
    void findScrapWithStoryByUserZero() {
        User user3 = userRepository.save(User.builder().build());
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Scrap> scraps = scrapRepository.findScrapWithStoryByUser(pageRequest, user3);

        assertThat(scraps.getContent().size()).isEqualTo(0);
    }
}
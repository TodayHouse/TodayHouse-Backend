package com.todayhouse.domain.image.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.story.domain.Story;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

class StoryImageRepositoryTest extends DataJpaBase {
    @Autowired
    StoryImageRepository storyImageRepository;

    @Autowired
    TestEntityManager em;

    @Test
    void test() {
        Story story = Story.builder().category(Story.Category.STORY).liked(1).title("hello").content("hello").build();
        em.persist(story);
        em.flush();
        em.clear();
        storyImageRepository.findFirstByStoryOrderByCreatedAtDesc(story);
    }
}
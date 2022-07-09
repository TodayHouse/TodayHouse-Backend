package com.todayhouse.domain.scrap.dao;

import com.todayhouse.domain.scrap.domain.Scrap;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long>, CustomScrapRepository {
    Optional<Scrap> findByUserAndStory(User user, Story story);

    Long countByStory(Story story);

    Long countByUser(User user);
}

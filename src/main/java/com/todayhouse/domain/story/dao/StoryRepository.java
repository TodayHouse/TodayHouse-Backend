package com.todayhouse.domain.story.dao;

import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoryRepository extends JpaRepository<Story, Long>, CustomStoryRepository {

    Slice<Story> findAllByUser(User user, Pageable pageable);

    @Query("select distinct st from Scrap sc inner join sc.story st where sc.story in :stories and sc.user =:user")
    List<Story> findScrapedByStoriesAndUser(@Param("stories") List<Story> stories, @Param("user") User user);

    @Query("select s from Story s inner join fetch s.user where s.id =:storyId")
    Optional<Story> findByIdWithUser(@Param("storyId") Long storyId);
}

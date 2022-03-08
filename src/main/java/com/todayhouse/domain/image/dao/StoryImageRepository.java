package com.todayhouse.domain.image.dao;

import com.todayhouse.domain.image.domain.StoryImage;
import com.todayhouse.domain.story.domain.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoryImageRepository extends JpaRepository<StoryImage, Long> {
    Optional<StoryImage> findFirstByStoryOrderByCreatedAtDesc(Story story);
    void deleteByFileName(String fileName);
}

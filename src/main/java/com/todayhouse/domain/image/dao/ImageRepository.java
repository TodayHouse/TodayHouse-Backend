package com.todayhouse.domain.image.dao;

import com.todayhouse.domain.image.domain.Image;
import com.todayhouse.domain.story.domain.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findFirstByStoryOrderByCreatedAtDesc(Story story);
}

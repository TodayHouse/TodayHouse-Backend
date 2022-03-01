package com.todayhouse.domain.story.dao;

import com.todayhouse.domain.story.domain.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long> {

    List<Story> findAllByOrderByIdDesc();
}

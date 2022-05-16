package com.todayhouse.domain.story.dao;

import com.todayhouse.domain.story.domain.StoryReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StoryReplyRepository extends JpaRepository<StoryReply, Long> {
}
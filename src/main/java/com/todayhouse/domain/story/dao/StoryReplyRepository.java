package com.todayhouse.domain.story.dao;

import com.todayhouse.domain.story.domain.StoryReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface StoryReplyRepository extends JpaRepository<StoryReply, Long> {
    @Query(value = "select r from StoryReply r " +
            " join fetch r.story s" +
            " join fetch r.user u" +
            " where s.id = :storyId"
            , countQuery = "select count(r) from StoryReply r inner join r.story s where r.id = :storyId")
    Page<StoryReply> findByStoryId(@Param("storyId") Long storyId, Pageable pageable);
}
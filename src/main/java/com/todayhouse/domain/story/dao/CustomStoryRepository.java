package com.todayhouse.domain.story.dao;

import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.dto.reqeust.StorySearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomStoryRepository {
    Page<Story> searchCondition(StorySearchRequest request, Pageable pageable);

}

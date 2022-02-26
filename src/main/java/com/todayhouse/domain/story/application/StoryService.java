package com.todayhouse.domain.story.application;

import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.dto.reqeust.StoryCreateRequest;
import com.todayhouse.domain.story.dto.reqeust.StoryUpdateRequest;
import com.todayhouse.domain.story.dto.response.StoryGetDetailResponse;
import com.todayhouse.domain.story.dto.response.StoryGetListResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StoryService {

    Long save(List<MultipartFile> multipartFile, StoryCreateRequest request);

    StoryGetDetailResponse findById(Long id);

    Story getStory(Long id);

    List<StoryGetListResponse> findAllDesc();

    Long update(Long id, StoryUpdateRequest request);

    void delete(Long id);
}

package com.todayhouse.domain.story.application;

import com.todayhouse.domain.story.dto.reqeust.StoryCreateRequest;
import com.todayhouse.domain.story.dto.reqeust.StoryUpdateRequest;
import com.todayhouse.domain.story.dto.response.StoryGetDetailResponse;
import com.todayhouse.domain.story.dto.response.StoryGetListResponse;
import nonapi.io.github.classgraph.fileslice.Slice;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StoryService {

    Long saveStory(List<MultipartFile> multipartFile, StoryCreateRequest request);

    Long saveImage(MultipartFile multipartFile, Long id);

    Slice<StoryGetListResponse> findAllDesc(Pageable pageable);

    StoryGetDetailResponse findById(Long id);

    List<StoryGetListResponse> findByUser();

    List<String> getStoryImageFileNamesAll();

    List<String> getImageInStory(Long id);

    byte[] getImage(String fileName);

    Long update(Long id, StoryUpdateRequest request);

    void deleteStory(Long id);

    void deleteImages(List<String> file);
}
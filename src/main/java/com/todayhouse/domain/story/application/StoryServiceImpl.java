package com.todayhouse.domain.story.application;

import com.todayhouse.domain.image.application.ImageService;
import com.todayhouse.domain.story.dao.StoryRepository;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.dto.reqeust.StoryCreateRequest;
import com.todayhouse.domain.story.dto.reqeust.StoryUpdateRequest;
import com.todayhouse.domain.story.dto.response.StoryGetDetailResponse;
import com.todayhouse.domain.story.dto.response.StoryGetListResponse;
import com.todayhouse.domain.story.exception.StoryNotFoundException;
import com.todayhouse.domain.user.application.CustomUserDetailService;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.infra.S3Storage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {

    private final StoryRepository storyRepository;
    private final FileService fileService;
    private final ImageService imageService;
    private final CustomUserDetailService customUserDetailService;

    @Override
    public Long save(List<MultipartFile> multipartFile, StoryCreateRequest request) {
        List<String> fileName = new ArrayList<>();
        if (!multipartFile.isEmpty()) {
            fileName = fileService.upload(multipartFile);
        }
        User user = (User) customUserDetailService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Story story = storyRepository.save(request.toEntity(user));
        imageService.save(fileName, story);
        return story.getId();
    }

    @Override
    public StoryGetDetailResponse findById(Long id) {
        return new StoryGetDetailResponse(this.getStory(id));
    }

    private Story getStory(Long id) {
        return storyRepository.findById(id).orElseThrow(StoryNotFoundException::new);
    }

    @Override
    public List<StoryGetListResponse> findAllDesc() {
        return storyRepository.findAllByOrderByIdDesc().stream()
                .map(story -> new StoryGetListResponse(story, imageService.findThumbnailUrl(story)))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getImageInStory(Long id) {
        return this.getStory(id).getImages().stream().map(image -> image.getFileName()).collect(Collectors.toList());
    }

    @Override
    public Long update(Long id, StoryUpdateRequest request) {
        Story story = storyRepository.findById(id).orElseThrow(StoryNotFoundException::new);
        story.update(request.getTitle(), request.getContent(), request.getCategory());
        return id;
    }

    @Override
    public void delete(Long id) {
        Story story = this.getStory(id);
        imageService.deleteStoryImages(story.getImages().stream().map(image -> image.getFileName()).collect(Collectors.toList()));
        storyRepository.delete(story);
    }
}
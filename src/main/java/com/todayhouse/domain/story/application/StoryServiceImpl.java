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
import com.todayhouse.infra.S3Storage.service.FileUploadService;
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
public class StoryServiceImpl implements StoryService{

    private final StoryRepository storyRepository;
    private final FileUploadService fileUploadService;
    private final ImageService imageService;
    private final CustomUserDetailService customUserDetailService;

    @Override
    public Long save(List<MultipartFile> multipartFile, StoryCreateRequest request) {
        List<String> fileName = new ArrayList<>();
        if (!multipartFile.isEmpty()){
            fileName = fileUploadService.upload(multipartFile);
        }
        User user = (User) customUserDetailService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Long id = storyRepository.save(request.toEntity(user)).getId();
        imageService.save(fileName, this.getStory(id));
        return id;
    }

    @Override
    public StoryGetDetailResponse findById(Long id){
        return new StoryGetDetailResponse(getStory(id));
    }

    @Override
    public Story getStory(Long id) {
        return storyRepository.findById(id).orElseThrow(StoryNotFoundException::new);
    }

    @Override
    public List<StoryGetListResponse> findAllDesc() {
        return storyRepository.findAllByOrderByIdDesc().stream()
                .map(StoryGetListResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public Long update(Long id, StoryUpdateRequest request){
        Story story = storyRepository.findById(id).orElseThrow(StoryNotFoundException::new);
        story.update(request.getTitle(), request.getContent(), request.getCategory());
        return id;
    }

    @Override
    public void delete(Long id){
        Story story = storyRepository.findById(id).orElseThrow(StoryNotFoundException::new);
        storyRepository.delete(story);
    }
}
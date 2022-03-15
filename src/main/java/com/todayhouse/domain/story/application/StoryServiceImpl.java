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
import com.todayhouse.domain.user.application.UserService;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import com.todayhouse.infra.S3Storage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {

    private final StoryRepository storyRepository;
    private final FileService fileService;
    private final ImageService imageService;
    private final UserService userService;
    private final CustomUserDetailService customUserDetailService;

    @Override
    public Long saveStory(List<MultipartFile> multipartFile, StoryCreateRequest request) {
        List<String> fileName = new ArrayList<>();
        if (!multipartFile.isEmpty()) {
            fileName = fileService.uploadImages(multipartFile);
        }
        User user = (User) customUserDetailService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Story story = storyRepository.save(request.toEntity(user));
        imageService.save(fileName, story);
        return story.getId();
    }

    @Override
    public Long saveImage(MultipartFile multipartFile, Long id){
        Story story = storyRepository.findById(id).orElseThrow(StoryNotFoundException::new);
        String fileName = fileService.uploadImage(multipartFile);
        imageService.saveOne(fileName, story);
        return id;
    }

    @Override
    public Slice<StoryGetListResponse> findAllDesc(Pageable pageable) {
        return storyRepository.findAllByOrderByIdDesc(pageable)
                .map(story -> new StoryGetListResponse(story, imageService.findThumbnailUrl(story)));
    }

    @Override
    public StoryGetDetailResponse findById(Long id) {
        return new StoryGetDetailResponse(this.getStory(id));
    }

    private Story getStory(Long id) {
        return storyRepository.findById(id).orElseThrow(StoryNotFoundException::new);
    }

    @Override
    public Slice<StoryGetListResponse> findByUser(Pageable pageable){
        User user = (User) customUserDetailService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        return storyRepository.findAllByUser(user, pageable)
                .map(story -> new StoryGetListResponse(story, imageService.findThumbnailUrl(story)));
    }

    public Slice<StoryGetListResponse> findByUserNickname(String nickname, Pageable pageable){
        User user = userService.findByNickname(nickname).orElseThrow(UserNotFoundException::new);
        return storyRepository.findAllByUser(user, pageable)
                .map(story -> new StoryGetListResponse(story, imageService.findThumbnailUrl(story)));
    }

    @Override
    public List<String> getStoryImageFileNamesAll(){
        return imageService.findStoryImageFileNamesAll();
    }

    @Override
    public List<String> getImageInStory(Long id) {
        return this.getStory(id).getImages().stream().map(image -> image.getFileName()).collect(Collectors.toList());
    }

    @Override
    public byte[] getImage(String fileName) {
        return fileService.getImage(fileName);
    }

    @Override
    public Long update(Long id, StoryUpdateRequest request) {
        Story story = storyRepository.findById(id).orElseThrow(StoryNotFoundException::new);
        story.update(request.getTitle(), request.getContent(), request.getCategory());
        return id;
    }

    @Override
    public void deleteStory(Long id) {
        Story story = this.getStory(id);
        imageService.deleteStoryImages(story.getImages().stream().map(image -> image.getFileName()).collect(Collectors.toList()));
        storyRepository.delete(story);
    }

    @Override
    public void deleteImages(List<String> file){
        imageService.deleteStoryImages(file);
    }
}
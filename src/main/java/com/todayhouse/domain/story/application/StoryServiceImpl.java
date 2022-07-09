package com.todayhouse.domain.story.application;

import com.todayhouse.domain.image.application.ImageService;
import com.todayhouse.domain.image.domain.Image;
import com.todayhouse.domain.story.dao.StoryRepository;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.dto.reqeust.StoryCreateRequest;
import com.todayhouse.domain.story.dto.reqeust.StorySearchRequest;
import com.todayhouse.domain.story.dto.reqeust.StoryUpdateRequest;
import com.todayhouse.domain.story.dto.response.StoryGetDetailResponse;
import com.todayhouse.domain.story.dto.response.StoryGetListResponse;
import com.todayhouse.domain.story.exception.StoryNotFoundException;
import com.todayhouse.domain.user.application.CustomUserDetailService;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import com.todayhouse.infra.S3Storage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StoryServiceImpl implements StoryService {

    private final FileService fileService;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
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
    public Long saveImage(MultipartFile multipartFile, Long id) {
        Story story = storyRepository.findById(id).orElseThrow(StoryNotFoundException::new);
        String fileName = fileService.uploadImage(multipartFile);
        imageService.saveOne(fileName, story);
        return id;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StoryGetListResponse> searchStory(StorySearchRequest request, Pageable pageable) {
        Page<Story> stories = storyRepository.searchCondition(request, pageable);
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElse(null);
        Map<Story, Boolean> scrapedStoriesMap = getScrapedStoriesMap(stories.getContent(), user);
        return stories.map(story -> new StoryGetListResponse(story, imageService.findThumbnailUrl(story), scrapedStoriesMap.getOrDefault(story, false)));
    }

    @Transactional(readOnly = true)
    @Override
    public StoryGetDetailResponse findById(Long id) {
        return new StoryGetDetailResponse(this.getStory(id), this.getStory(id).getImages().stream().map(image -> fileService.changeFileNameToUrl(image.getFileName())).collect(Collectors.toList()));
    }

    private Story getStory(Long id) {
        return storyRepository.findById(id).orElseThrow(StoryNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<StoryGetListResponse> findByUser(Pageable pageable) {
        User user = (User) customUserDetailService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        Slice<Story> stories = storyRepository.findAllByUser(user, pageable);
        Map<Story, Boolean> scrapedStoriesMap = getScrapedStoriesMap(stories.getContent(), user);
        return stories.map(story -> new StoryGetListResponse(story, imageService.findThumbnailUrl(story), scrapedStoriesMap.getOrDefault(story, false)));
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<StoryGetListResponse> findByUserNickname(String nickname, Pageable pageable) {
        User user = userRepository.findByNickname(nickname).orElseThrow(UserNotFoundException::new);
        Slice<Story> stories = storyRepository.findAllByUser(user, pageable);
        Map<Story, Boolean> scrapedStoriesMap = getScrapedStoriesMap(stories.getContent(), user);
        return stories.map(story -> new StoryGetListResponse(story, imageService.findThumbnailUrl(story), scrapedStoriesMap.getOrDefault(story, false)));
    }

    @Override
    public List<String> getStoryImageFileNamesAll() {
        return imageService.findStoryImageFileNamesAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getImageInStory(Long id) {
        return this.getStory(id).getImages().stream().map(Image::getFileName).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
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
        List<String> fileNames = story.getImages().stream().map(Image::getFileName).collect(Collectors.toList());
        imageService.deleteStoryImages(fileNames);
        fileService.delete(fileNames);
        storyRepository.delete(story);
    }

    @Override
    public void deleteImages(List<String> file) {
        imageService.deleteStoryImages(file);
        fileService.delete(file);
    }

    private Map<Story, Boolean> getScrapedStoriesMap(List<Story> stories, User user) {
        List<Story> scrapedStories = storyRepository.findScrapedByStoriesAndUser(stories, user);
        return makeStoryListToBooleanMap(scrapedStories);
    }

    private Map<Story, Boolean> makeStoryListToBooleanMap(List<Story> stories) {
        Map<Story, Boolean> map = new HashMap<>();
        stories.forEach(story -> map.put(story, true));
        return map;
    }
}
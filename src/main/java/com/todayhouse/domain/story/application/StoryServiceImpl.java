package com.todayhouse.domain.story.application;

import com.todayhouse.domain.image.application.ImageService;
import com.todayhouse.domain.image.dao.StoryImageRepository;
import com.todayhouse.domain.image.domain.Image;
import com.todayhouse.domain.image.domain.StoryImage;
import com.todayhouse.domain.likes.dao.LikesStoryRepository;
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
import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;
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
    private final StoryImageRepository storyImageRepository;
    private final CustomUserDetailService customUserDetailService;

    private final LikesStoryRepository likesStoryRepository;

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

    @Override
    public StoryGetDetailResponse findById(User user, Long id) {
        Story story = getStoryWithUser(id);
        List<String> urls = storyImageRepository.findByStory(story).stream()
                .map(image -> fileService.changeFileNameToUrl(image.getFileName())).collect(Collectors.toList());
        story.increaseView();
        StoryGetDetailResponse storyGetDetailResponse = new StoryGetDetailResponse(story, urls);
        if (user != null) {
            storyGetDetailResponse.liked(likesStoryRepository.existsByUser_EmailAndStory_Id(user.getEmail(), id));

        }
        return storyGetDetailResponse;
    }

    private Story getStory(Long id) {
        return storyRepository.findById(id).orElseThrow(StoryNotFoundException::new);
    }

    private Story getStoryWithUser(Long storyId) {
        return storyRepository.findByIdWithUser(storyId).orElseThrow(StoryNotFoundException::new);
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
        User user = userRepository.findByNickname(nickname).orElseThrow(() -> new BaseException(BaseResponseStatus.USER_NOT_FOUND));
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
        Story story = getStory(id);
        List<StoryImage> storyImages = storyImageRepository.findByStory(story);
        return storyImages.stream().map(Image::getFileName).collect(Collectors.toList());
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
        Story story = getStory(id);
        List<String> fileNames = storyImageRepository.findByStory(story).stream().map(Image::getFileName).collect(Collectors.toList());
        deleteImages(fileNames);
        storyRepository.delete(story);
    }

    @Override
    public void deleteImages(List<String> fileNames) {
        imageService.deleteStoryImages(fileNames);
        fileService.delete(fileNames);
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
package com.todayhouse.domain.likes.application;

import com.todayhouse.domain.likes.dao.LikesStoryRepository;
import com.todayhouse.domain.likes.domain.LikesStory;
import com.todayhouse.domain.likes.domain.LikesType;
import com.todayhouse.domain.likes.dto.LikesRequest;
import com.todayhouse.domain.likes.dto.LikesResponse;
import com.todayhouse.domain.likes.dto.UnLikesRequest;
import com.todayhouse.domain.likes.dto.UnLikesResponse;
import com.todayhouse.domain.story.dao.StoryRepository;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LikesStoryServiceImpl implements LikesService {
    private final UserRepository userRepository;
    private final LikesStoryRepository likesStoryRepository;
    private final StoryRepository storyRepository;

    @Override
    public LikesResponse likes(User principal, LikesRequest request) {
        User user = userRepository.findByEmail(principal.getEmail()).orElseThrow(() -> new UserNotFoundException());

        Optional<LikesStory> likesStory = likesStoryRepository.findByUser_IdAndStory_Id(user.getId(), request.getTypeId());
        if (likesStory.isPresent()) {
            throw new BaseException(BaseResponseStatus.LIKES_DUPLICATE_EXCEPTION);
        }
        Story story = storyRepository.findById(request.getTypeId()).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_STORY));
        LikesStory save = likesStoryRepository.save(new LikesStory(user, story));
        story.getLikesStories().add(save);
        return new LikesResponse(likesStoryRepository.countByStory_Id(story.getId()), true);
    }

    @Override
    public UnLikesResponse unlikes(User principal, UnLikesRequest request) {
        User user = userRepository.findByEmail(principal.getEmail()).orElseThrow(() -> new UserNotFoundException());

        LikesStory likesStory = likesStoryRepository
                .findByUser_IdAndStory_Id(user.getId(), request.getTypeId())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_STORY));

        Story story = likesStory.getStory();
        Long storyId = story.getId();
        if (likesStory.getUser().getId().equals(user.getId())) {
            story.getLikesStories().remove(likesStory);
            likesStoryRepository.deleteById(likesStory.getId());
        }
        return new UnLikesResponse(likesStoryRepository.countByStory_Id(storyId), false);
    }

    @Override
    public boolean isMatching(LikesType likesType) {
        return likesType == LikesType.STORY;
    }
}

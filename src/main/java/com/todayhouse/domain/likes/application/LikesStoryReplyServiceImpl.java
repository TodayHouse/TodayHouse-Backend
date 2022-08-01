package com.todayhouse.domain.likes.application;

import com.todayhouse.domain.likes.dao.LikesStoryReplyRepository;
import com.todayhouse.domain.likes.domain.LikesStoryReply;
import com.todayhouse.domain.likes.domain.LikesType;
import com.todayhouse.domain.likes.dto.LikesRequest;
import com.todayhouse.domain.likes.dto.LikesResponse;
import com.todayhouse.domain.likes.dto.UnLikesRequest;
import com.todayhouse.domain.likes.dto.UnLikesResponse;
import com.todayhouse.domain.story.dao.StoryReplyRepository;
import com.todayhouse.domain.story.domain.StoryReply;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesStoryReplyServiceImpl implements LikesService {

    private final UserRepository userRepository;

    private final StoryReplyRepository replyRepository;

    private final LikesStoryReplyRepository likesStoryReplyRepository;

    @Override
    public LikesResponse likes(User principal, LikesRequest request) {

        Optional<LikesStoryReply> optionalLikesStoryReply = likesStoryReplyRepository.findByIdAndUserEmail(request.getTypeId(), principal.getEmail());

        if (optionalLikesStoryReply.isEmpty()) {
            User user = userRepository.findByEmail(principal.getEmail()).orElseThrow(UserNotFoundException::new);
            StoryReply storyReply = replyRepository.findById(request.getTypeId()).orElseThrow(() -> new BaseException(BaseResponseStatus.REPLY_NOT_FOUND));

            LikesStoryReply likesStoryReply = new LikesStoryReply(user, storyReply);
            LikesStoryReply save = likesStoryReplyRepository.save(likesStoryReply);
            storyReply.getLikesStoryReplies().add(save);
            return new LikesResponse(likesStoryReplyRepository.countByStoryReply_Id(request.getTypeId()), true);
        } else {
            throw new BaseException(BaseResponseStatus.LIKES_DUPLICATE_EXCEPTION);
        }

    }

    @Override
    public UnLikesResponse unlikes(User principal, UnLikesRequest request) {
        LikesStoryReply likesStoryReply = likesStoryReplyRepository
                .findByIdAndUserEmail(request.getTypeId(), principal.getEmail())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.LIKES_NOT_FOUND));

        if (principal.getEmail().equals(likesStoryReply.getUser().getEmail())) {
            likesStoryReply.getStoryReply().getLikesStoryReplies().remove(likesStoryReply);
            likesStoryReplyRepository.delete(likesStoryReply);
            return new UnLikesResponse(likesStoryReplyRepository.countByStoryReply_Id(request.getTypeId()), false);
        }
        throw new BaseException(BaseResponseStatus.LIKES_DELETE_EXCEPTION);
    }

    @Override
    public boolean isMatching(LikesType likesType) {
        return likesType == LikesType.STORY_REPLY;
    }
}

package com.todayhouse.domain.story.application;

import com.todayhouse.domain.likes.dao.LikesStoryReplyRepository;
import com.todayhouse.domain.likes.domain.LikesStoryReply;
import com.todayhouse.domain.story.dao.StoryReplyRepository;
import com.todayhouse.domain.story.dao.StoryRepository;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.domain.StoryReply;
import com.todayhouse.domain.story.dto.reqeust.ReplyCreateRequest;
import com.todayhouse.domain.story.dto.reqeust.ReplyDeleteRequest;
import com.todayhouse.domain.story.dto.response.ReplyCreateResponse;
import com.todayhouse.domain.story.dto.response.ReplyGetResponse;
import com.todayhouse.domain.story.exception.ReplyNotFoundException;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static com.todayhouse.global.error.BaseResponseStatus.REPLY_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class StoryReplyServiceImpl implements StoryReplyService {
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final StoryReplyRepository replyRepository;

    private final LikesStoryReplyRepository likesStoryReplyRepository;


    @Override
    public void deleteReply(User user, ReplyDeleteRequest request) {
        Optional<StoryReply> byId = replyRepository.findById(request.getStoryId());
        StoryReply storyReply = byId.orElseThrow(() -> new ReplyNotFoundException(REPLY_NOT_FOUND));
        user = userRepository.findByEmail(user.getEmail()).orElseThrow(UserNotFoundException::new);
        if (!user.getId().equals(storyReply.getUser().getId())) {
            throw new RuntimeException();
        }
        replyRepository.delete(storyReply);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ReplyGetResponse> findReplies(@AuthenticationPrincipal User user, Long storyId, @PageableDefault Pageable pageable) {
        Page<StoryReply> storyReplies = replyRepository.findByStoryId(storyId, pageable);
        Page<ReplyGetResponse> map = storyReplies.map(r -> new ReplyGetResponse(
                r.getId(),
                r.getContent(),
                r.getCreatedAt(),
                r.getUser(),
                r.getLikesStoryReplies().size())
        );
        if (user == null) {
            return map;
        } else {
            Set<Long> userLikes = likesStoryReplyRepository.findIdsByUserEmail(user.getEmail());
            map.forEach(replyGetResponse -> replyGetResponse.setLiked(userLikes.contains(replyGetResponse.getId())));
        }

        return map;

    }

    @Override
    public ReplyCreateResponse replyStory(User user, ReplyCreateRequest request) {
        Story byId = storyRepository.getById(request.getStoryId());
        user = userRepository.findByEmail(user.getEmail()).orElseThrow(UserNotFoundException::new);

        StoryReply storyReply = StoryReply.builder()
                .content(request.getContent())
                .story(byId)
                .user(user)
                .build();
        StoryReply save = replyRepository.save(storyReply);
        return ReplyCreateResponse.builder()
                .id(save.getId())
                .nickname(save.getUser().getNickname())
                .content(save.getContent())
                .createdDate(save.getCreatedAt())
                .isMine(Boolean.TRUE)
                .build();

    }
}

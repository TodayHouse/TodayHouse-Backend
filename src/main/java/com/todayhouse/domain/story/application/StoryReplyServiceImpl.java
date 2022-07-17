package com.todayhouse.domain.story.application;

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
import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.Optional;

import static com.todayhouse.global.error.BaseResponseStatus.REPLY_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class StoryReplyServiceImpl implements StoryReplyService {
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final StoryReplyRepository replyRepository;


    @Override
    public void deleteReply(User user, Long replyId) {
        Optional<StoryReply> byId = replyRepository.findById(replyId);
        StoryReply storyReply = byId.orElseThrow(() -> new ReplyNotFoundException(REPLY_NOT_FOUND));

        Optional<User> user1 = userRepository.findByEmail(user.getEmail());
        User user2 = user1.orElseThrow();
        if (!user2.getId().equals(storyReply.getUser().getId())) {
            throw new RuntimeException();
        }
        replyRepository.delete(storyReply);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ReplyGetResponse> findReplies(User user, Long storyId, @PageableDefault Pageable pageable) {
        Page<StoryReply> storyReplies = replyRepository.findByStoryId(storyId, pageable);

        Page<ReplyGetResponse> map = storyReplies.map(r -> new ReplyGetResponse(r.getId(), r.getContent(), r.getCreatedAt(), r.getUser()));
        Long userId;

        if (user == null) {
            userId = null;
        } else {
            Optional<User> byEmail = userRepository.findByEmail(user.getEmail());
            User user1 = byEmail.orElseThrow();
            userId = user1.getId();
        }
        Long finalUserId = userId;
        map.forEach(replyGetResponse -> replyGetResponse.IsMine(finalUserId));

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

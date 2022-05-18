package com.todayhouse.domain.story.application;

import com.todayhouse.domain.story.dao.StoryReplyRepository;
import com.todayhouse.domain.story.dao.StoryRepository;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.domain.StoryReply;
import com.todayhouse.domain.story.dto.reqeust.DeleteReplyRequest;
import com.todayhouse.domain.story.dto.reqeust.CreateReplyRequest;
import com.todayhouse.domain.story.dto.response.CreateReplyResponse;
import com.todayhouse.domain.story.dto.response.ReplyGetResponse;
import com.todayhouse.domain.story.exception.ReplyNotFoundException;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void deleteReply(User user, DeleteReplyRequest request) {
        Optional<StoryReply> byId = replyRepository.findById(request.getStoryId());
        StoryReply storyReply = byId.orElseThrow(() -> new ReplyNotFoundException(REPLY_NOT_FOUND));
        user = userRepository.findByEmail(user.getEmail()).orElseThrow(UserNotFoundException::new);
        if (!user.getId().equals(storyReply.getUser().getId())) {
            throw new RuntimeException();
        }
        replyRepository.delete(storyReply);
    }


    @Override
    public Page<ReplyGetResponse> findReplies(Long storyId, Pageable pageable) {
        PageRequest of = PageRequest.of(0, 10);
        Page<StoryReply> storyReplies = replyRepository.findByStoryId(storyId, of);
        Page<ReplyGetResponse> map = storyReplies.map(r -> new ReplyGetResponse(r.getId(), r.getContent(), r.getCreatedDate(), r.getUser()));
        return map;
    }

    @Override
    public CreateReplyResponse replyStory(User user, CreateReplyRequest request) {
        Story byId = storyRepository.getById(request.getStoryId());
        user = userRepository.findByEmail(user.getEmail()).orElseThrow(UserNotFoundException::new);

        StoryReply storyReply = StoryReply.builder()
                .content(request.getContent())
                .story(byId)
                .user(user)
                .build();
        StoryReply save = replyRepository.save(storyReply);
        CreateReplyResponse response = CreateReplyResponse.builder()
                .id(save.getId())
                .nickname(save.getUser().getNickname())
                .content(save.getContent())
                .createdDate(save.getCreatedDate())
                .isMine(Boolean.TRUE)
                .build();
        return response;

    }
}

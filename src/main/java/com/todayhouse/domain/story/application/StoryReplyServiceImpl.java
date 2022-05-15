package com.todayhouse.domain.story.application;

import com.todayhouse.domain.story.dao.StoryReplyRepository;
import com.todayhouse.domain.story.dao.StoryRepository;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.domain.StoryReply;
import com.todayhouse.domain.story.dto.reqeust.DeleteReplyRequest;
import com.todayhouse.domain.story.dto.reqeust.StoryReplyRequest;
import com.todayhouse.domain.story.dto.response.CreateReplyResponse;
import com.todayhouse.domain.story.exception.ReplyNotFoundException;
import com.todayhouse.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.todayhouse.global.error.BaseResponseStatus.REPLY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class StoryReplyServiceImpl implements StoryReplyService {
    private final StoryRepository storyRepository;
    private final StoryReplyRepository replyRepository;

    @Override
    public void deleteReply(User user, DeleteReplyRequest request) {
        Optional<StoryReply> byId = replyRepository.findById(request.getId());
        StoryReply storyReply = byId.orElseThrow(() -> new ReplyNotFoundException(REPLY_NOT_FOUND));

        if (!user.getId().equals(storyReply.getUser().getId())) {
            throw new RuntimeException();
        }
        replyRepository.delete(storyReply);
    }


    @Override
    public CreateReplyResponse replyStory(User user, StoryReplyRequest request) {
        Story byId = storyRepository.getById(request.getStoryId());
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

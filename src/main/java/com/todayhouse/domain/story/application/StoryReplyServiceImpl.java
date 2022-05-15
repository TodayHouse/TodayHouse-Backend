package com.todayhouse.domain.story.application;

import com.todayhouse.domain.story.dao.StoryRepository;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.domain.StoryReply;
import com.todayhouse.domain.story.dto.reqeust.StoryReplyRequest;
import com.todayhouse.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoryReplyServiceImpl implements StoryReplyService {
    private final StoryRepository storyRepository;

    @Override
    public void replyStory(User user, StoryReplyRequest request) {
        Story byId = storyRepository.getById(request.getStoryId());
        StoryReply storyReply = StoryReply.builder()
                .content(request.getContent())
                .story(byId)
                .user(user)
                .build();

    }
}

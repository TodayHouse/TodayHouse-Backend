package com.todayhouse.domain.story.application;

import com.todayhouse.domain.story.dto.reqeust.StoryReplyRequest;
import com.todayhouse.domain.user.domain.User;


public interface StoryReplyService {
    void replyStory(User user, StoryReplyRequest request);

}

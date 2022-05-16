package com.todayhouse.domain.story.application;

import com.todayhouse.domain.story.dto.reqeust.DeleteReplyRequest;
import com.todayhouse.domain.story.dto.reqeust.CreateReplyRequest;
import com.todayhouse.domain.story.dto.response.CreateReplyResponse;
import com.todayhouse.domain.user.domain.User;


public interface StoryReplyService {
    CreateReplyResponse replyStory(User user, CreateReplyRequest request);

    void deleteReply(User user, DeleteReplyRequest request);
}

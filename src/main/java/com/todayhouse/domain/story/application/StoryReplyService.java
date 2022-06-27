package com.todayhouse.domain.story.application;

import com.todayhouse.domain.story.dto.reqeust.ReplyCreateRequest;
import com.todayhouse.domain.story.dto.reqeust.ReplyDeleteRequest;
import com.todayhouse.domain.story.dto.response.ReplyCreateResponse;
import com.todayhouse.domain.story.dto.response.ReplyGetResponse;
import com.todayhouse.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface StoryReplyService {
    ReplyCreateResponse replyStory(User user, ReplyCreateRequest request);

    void deleteReply(User user, ReplyDeleteRequest request);

    Page<ReplyGetResponse> findReplies(Long storyId, Pageable pageable);
}

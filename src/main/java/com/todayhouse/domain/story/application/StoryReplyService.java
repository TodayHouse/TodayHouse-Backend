package com.todayhouse.domain.story.application;

import com.todayhouse.domain.story.dto.reqeust.ReplyCreateRequest;
import com.todayhouse.domain.story.dto.reqeust.ReplyDeleteRequest;
import com.todayhouse.domain.story.dto.response.ReplyCreateResponse;
import com.todayhouse.domain.story.dto.response.ReplyGetResponse;
import com.todayhouse.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;


public interface StoryReplyService {
    @Transactional(readOnly = true)
    Page<ReplyGetResponse> findReplies(@AuthenticationPrincipal User user, Long storyId, @PageableDefault Pageable pageable);

    ReplyCreateResponse replyStory(User user, ReplyCreateRequest request);

    void deleteReply(User user, ReplyDeleteRequest request);

}

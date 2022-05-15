package com.todayhouse.domain.story.api;

import com.todayhouse.domain.story.application.StoryReplyService;
import com.todayhouse.domain.story.dto.reqeust.StoryReplyRequest;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("story")
@RestController
@RequiredArgsConstructor
public class StoryReplyController {
    private final StoryReplyService replyService;

    @PostMapping("reply")
    public BaseResponse<String> replyPost(@AuthenticationPrincipal User user, StoryReplyRequest request) {
        replyService.replyStory(user, request);
        return new BaseResponse<>("댓글이 작성되었습니다.");

    }
}

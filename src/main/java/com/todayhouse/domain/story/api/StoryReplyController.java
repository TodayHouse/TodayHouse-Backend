package com.todayhouse.domain.story.api;

import com.todayhouse.domain.story.application.StoryReplyService;
import com.todayhouse.domain.story.dto.reqeust.DeleteReplyRequest;
import com.todayhouse.domain.story.dto.reqeust.StoryReplyRequest;
import com.todayhouse.domain.story.dto.response.CreateReplyResponse;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.global.error.BaseResponseStatus;
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

    @PostMapping("createReply")
    public BaseResponse<CreateReplyResponse> replyPost(@AuthenticationPrincipal User user, StoryReplyRequest request) {
        CreateReplyResponse response = replyService.replyStory(user, request);
        return new BaseResponse<>(response);

    }

    @PostMapping("deleteReply")
    public BaseResponse<String> deleteReply(@AuthenticationPrincipal User user, DeleteReplyRequest request) {
        replyService.deleteReply(user, request);
        return new BaseResponse<>("삭제 완료");
    }
}

package com.todayhouse.domain.story.api;

import com.todayhouse.domain.story.application.StoryReplyService;
import com.todayhouse.domain.story.dto.reqeust.CreateReplyRequest;
import com.todayhouse.domain.story.dto.reqeust.DeleteReplyRequest;
import com.todayhouse.domain.story.dto.response.CreateReplyResponse;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/stories")
@RestController
@RequiredArgsConstructor
public class StoryReplyController {
    private final StoryReplyService replyService;

    @PostMapping("/reply")
    public BaseResponse<CreateReplyResponse> replyPost(@RequestBody CreateReplyRequest request, @AuthenticationPrincipal User user) {
        CreateReplyResponse response = replyService.replyStory(user, request);
        return new BaseResponse<>(response);

    }

    @DeleteMapping("/reply")
    public BaseResponse<String> deleteReply(@AuthenticationPrincipal User user, @RequestBody DeleteReplyRequest request) {
        replyService.deleteReply(user, request);
        return new BaseResponse<>("삭제 완료");
    }

}



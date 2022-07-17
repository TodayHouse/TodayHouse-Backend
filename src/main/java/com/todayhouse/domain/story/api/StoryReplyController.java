package com.todayhouse.domain.story.api;

import com.todayhouse.domain.story.application.StoryReplyService;
import com.todayhouse.domain.story.dto.reqeust.ReplyCreateRequest;
import com.todayhouse.domain.story.dto.reqeust.ReplyDeleteRequest;
import com.todayhouse.domain.story.dto.response.ReplyCreateResponse;
import com.todayhouse.domain.story.dto.response.ReplyGetResponse;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.global.common.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/stories")
@RestController
@RequiredArgsConstructor
public class StoryReplyController {
    private final StoryReplyService replyService;

    @PostMapping("/reply")
    public BaseResponse<ReplyCreateResponse> replyPost(@RequestBody ReplyCreateRequest request, @AuthenticationPrincipal User user) {
        ReplyCreateResponse response = replyService.replyStory(user, request);
        return new BaseResponse<>(response);

    }


    @DeleteMapping("/reply/{id}")
    public BaseResponse<String> deleteReply(@AuthenticationPrincipal User user, @PathVariable Long id) {
        replyService.deleteReply(user, id);
        return new BaseResponse<>("삭제 완료");
    }


    @GetMapping("/reply/{storyId}")
    public BaseResponse<PageDto<ReplyGetResponse>> findReplies(@PathVariable Long storyId, Pageable pageable, @AuthenticationPrincipal User user) {

        return new BaseResponse<>(new PageDto<>(replyService.findReplies(user, storyId, pageable)));
    }

}



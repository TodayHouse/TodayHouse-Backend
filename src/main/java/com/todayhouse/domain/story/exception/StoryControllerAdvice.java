package com.todayhouse.domain.story.exception;

import com.todayhouse.global.common.BaseResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StoryControllerAdvice {

    @ExceptionHandler(ReplyNotFoundException.class)
    public BaseResponse<String> ReplyNotFoundHandle() {
        return new BaseResponse<>("댓글이 존재하지 않습니다.");
    }

}

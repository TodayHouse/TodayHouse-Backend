package com.todayhouse.domain.story.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class ReplyNotFoundException extends BaseException {

    public ReplyNotFoundException(BaseResponseStatus status) {
        super(status);
    }
}

package com.todayhouse.domain.story.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class StoryNotFoundException extends BaseException {

    public StoryNotFoundException() {
        super(BaseResponseStatus.NOT_FOUND_STORY);
    }
}

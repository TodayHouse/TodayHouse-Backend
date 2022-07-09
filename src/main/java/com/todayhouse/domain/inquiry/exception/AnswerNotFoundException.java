package com.todayhouse.domain.inquiry.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class AnswerNotFoundException extends BaseException {
    public AnswerNotFoundException() {
        super(BaseResponseStatus.ANSWER_NOT_FOUND);
    }
}

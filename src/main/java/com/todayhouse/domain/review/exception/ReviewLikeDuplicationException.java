package com.todayhouse.domain.review.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class ReviewLikeDuplicationException extends BaseException {
    public ReviewLikeDuplicationException() {
        super(BaseResponseStatus.REVIEW_DUPLICATE);
    }
}

package com.todayhouse.domain.review.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class ReviewDuplicateException extends BaseException {
    public ReviewDuplicateException() {
        super(BaseResponseStatus.REVIEW_DUPLICATE);
    }
}

package com.todayhouse.domain.review.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class ReviewNotFoundException extends BaseException {
    public ReviewNotFoundException() {
        super(BaseResponseStatus.REVIEW_NOT_FOUND);
    }
}

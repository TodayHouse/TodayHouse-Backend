package com.todayhouse.domain.review.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class ReviewLikeNotFoundException extends BaseException {
    public ReviewLikeNotFoundException() {
        super(BaseResponseStatus.REVIEW_LIKE_NOT_FOUND);
    }
}

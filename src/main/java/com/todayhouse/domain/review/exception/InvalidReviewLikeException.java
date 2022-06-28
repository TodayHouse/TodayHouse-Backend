package com.todayhouse.domain.review.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class InvalidReviewLikeException extends BaseException {
    public InvalidReviewLikeException() {
        super(BaseResponseStatus.INVALID_REVIEW_LIKE);
    }
}

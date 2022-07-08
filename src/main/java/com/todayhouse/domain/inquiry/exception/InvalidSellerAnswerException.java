package com.todayhouse.domain.inquiry.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class InvalidSellerAnswerException extends BaseException {
    public InvalidSellerAnswerException() {
        super(BaseResponseStatus.INVALID_SELLER_ANSWER);
    }
}

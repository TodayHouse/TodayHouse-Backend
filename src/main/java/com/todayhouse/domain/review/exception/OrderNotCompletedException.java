package com.todayhouse.domain.review.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class OrderNotCompletedException extends BaseException {
    public OrderNotCompletedException() {
        super(BaseResponseStatus.ORDER_NOT_COMPLETED);
    }
}

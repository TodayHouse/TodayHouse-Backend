package com.todayhouse.domain.order.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class OrderNotFoundException extends BaseException {
    public OrderNotFoundException() {
        super(BaseResponseStatus.ORDER_NOT_FOUND_EXCEPTION);
    }
}

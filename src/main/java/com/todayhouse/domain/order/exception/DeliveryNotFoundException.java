package com.todayhouse.domain.order.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class DeliveryNotFoundException extends BaseException {
    public DeliveryNotFoundException() {
        super(BaseResponseStatus.DELIVERY_NOT_FOUND);
    }
}

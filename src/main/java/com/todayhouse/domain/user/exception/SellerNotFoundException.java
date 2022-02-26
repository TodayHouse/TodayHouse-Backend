package com.todayhouse.domain.user.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class SellerNotFoundException extends BaseException {
    public SellerNotFoundException() {
        super(BaseResponseStatus.SELLER_NOT_FOUND);
    }
}

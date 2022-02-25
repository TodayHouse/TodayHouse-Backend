package com.todayhouse.domain.user.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class SellerExistException extends BaseException {
    public SellerExistException() {
        super(BaseResponseStatus.SELLER_EXIST);
    }
}

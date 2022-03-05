package com.todayhouse.domain.product.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class ChildOptionNotFoundException extends BaseException {
    public ChildOptionNotFoundException() {
        super(BaseResponseStatus.CHILD_OPTION_NOT_FOUND);
    }
}

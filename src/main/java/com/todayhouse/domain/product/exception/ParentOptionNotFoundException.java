package com.todayhouse.domain.product.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class ParentOptionNotFoundException extends BaseException {
    public ParentOptionNotFoundException() {
        super(BaseResponseStatus.PARENT_OPTION_NOT_FOUND);
    }
}

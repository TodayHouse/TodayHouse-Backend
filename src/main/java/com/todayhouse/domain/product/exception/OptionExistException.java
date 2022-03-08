package com.todayhouse.domain.product.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class OptionExistException extends BaseException {
    public OptionExistException() {
        super(BaseResponseStatus.PARENT_OPTION_EXIST);
    }
}

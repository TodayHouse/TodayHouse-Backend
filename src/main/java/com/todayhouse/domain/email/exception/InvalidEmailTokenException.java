package com.todayhouse.domain.email.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class InvalidEmailTokenException extends BaseException {
    public InvalidEmailTokenException() {
        super(BaseResponseStatus.INVALID_EMAIL_TOEKN);
    }
}

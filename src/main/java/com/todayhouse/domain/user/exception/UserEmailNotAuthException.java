package com.todayhouse.domain.user.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class UserEmailNotAuthException extends BaseException {
    public UserEmailNotAuthException() {
        super(BaseResponseStatus.INVALID_AUTH_EMAIL);
    }
}

package com.todayhouse.domain.user.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class UserEmailNotFountException extends BaseException {
    public UserEmailNotFountException() {
        super(BaseResponseStatus.NOT_FOUND_EMAIL);
    }
}

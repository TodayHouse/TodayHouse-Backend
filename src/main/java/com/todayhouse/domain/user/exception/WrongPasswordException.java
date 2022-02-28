package com.todayhouse.domain.user.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class WrongPasswordException extends BaseException {
    public WrongPasswordException() {
        super(BaseResponseStatus.WRONG_PASSWORD);
    }
}

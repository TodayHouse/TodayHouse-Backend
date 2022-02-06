package com.todayhouse.domain.user.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class SignupPasswordException extends BaseException {
    public SignupPasswordException() {
        super(BaseResponseStatus.WRONG_SIGNUP_PASSWORD);
    }
}

package com.todayhouse.domain.user.oauth.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class InvalidAuthException extends BaseException {
    public InvalidAuthException() {
        super(BaseResponseStatus.INVALID_AUTH);
    }
}

package com.todayhouse.domain.user.oauth.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class AuthNotGuestException extends BaseException {
    public AuthNotGuestException() {
        super(BaseResponseStatus.NOT_GUEST_ACCESS);
    }
}

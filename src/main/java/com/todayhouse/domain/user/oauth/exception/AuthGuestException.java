package com.todayhouse.domain.user.oauth.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class AuthGuestException extends BaseException {
    public AuthGuestException() {
        super(BaseResponseStatus.IS_GUEST_ACCESS);
    }
}

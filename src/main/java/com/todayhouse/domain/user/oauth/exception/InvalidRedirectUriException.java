package com.todayhouse.domain.user.oauth.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class InvalidRedirectUriException extends BaseException {
    public InvalidRedirectUriException() {
        super(BaseResponseStatus.INVALID_REDIRECT_URI);
    }
}

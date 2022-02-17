package com.todayhouse.domain.user.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException() {
        super(BaseResponseStatus.USER_NOT_FOUND);
    }
}

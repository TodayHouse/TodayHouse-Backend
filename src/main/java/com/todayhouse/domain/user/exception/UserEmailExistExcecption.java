package com.todayhouse.domain.user.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class UserEmailExistExcecption extends BaseException {
    public UserEmailExistExcecption() {
        super(BaseResponseStatus.POST_USER_EXISTS_EMAIL);
    }
}

package com.todayhouse.domain.user.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class UserNicknameExistException extends BaseException {
    public UserNicknameExistException() {
        super(BaseResponseStatus.POST_USER_EXISTS_EMAIL);
    }
}

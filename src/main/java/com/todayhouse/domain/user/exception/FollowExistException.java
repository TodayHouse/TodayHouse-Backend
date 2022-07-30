package com.todayhouse.domain.user.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class FollowExistException extends BaseException {
    public FollowExistException() {
        super(BaseResponseStatus.FOLLOW_EXIST);
    }
}

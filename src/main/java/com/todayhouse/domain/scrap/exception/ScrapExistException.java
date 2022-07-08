package com.todayhouse.domain.scrap.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class ScrapExistException extends BaseException {
    public ScrapExistException() {
        super(BaseResponseStatus.SCRAP_EXIST);
    }
}

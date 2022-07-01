package com.todayhouse.infra.S3Storage.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class InvalidUrlException extends BaseException {
    public InvalidUrlException() {
        super(BaseResponseStatus.INVALID_URL_EXCEPTION);
    }
}

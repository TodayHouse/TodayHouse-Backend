package com.todayhouse.infra.S3Storage.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class InvalidFileExtensionException extends BaseException {

    public InvalidFileExtensionException(BaseResponseStatus status) { super(status); }
}

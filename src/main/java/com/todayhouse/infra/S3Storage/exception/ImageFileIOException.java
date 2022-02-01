package com.todayhouse.infra.S3Storage.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class ImageFileIOException extends BaseException {

    public ImageFileIOException(BaseResponseStatus status) {
        super(status);
    }
}

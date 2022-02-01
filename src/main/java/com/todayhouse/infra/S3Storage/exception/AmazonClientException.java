package com.todayhouse.infra.S3Storage.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class AmazonClientException extends BaseException {

    public AmazonClientException(BaseResponseStatus status) {
        super(status);
    }
}

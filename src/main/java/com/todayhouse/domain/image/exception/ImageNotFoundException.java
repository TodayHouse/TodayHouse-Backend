package com.todayhouse.domain.image.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class ImageNotFoundException extends BaseException {
    public ImageNotFoundException() {
        super(BaseResponseStatus.NOT_FOUND_IMAGE);
    }
}

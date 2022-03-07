package com.todayhouse.domain.product.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class ImageCannotChangeException extends BaseException {
    public ImageCannotChangeException() {
        super(BaseResponseStatus.PRODUCT_IMAGE_FIXED);
    }
}

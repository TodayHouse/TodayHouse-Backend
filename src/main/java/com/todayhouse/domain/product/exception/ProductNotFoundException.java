package com.todayhouse.domain.product.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class ProductNotFoundException extends BaseException {
    public ProductNotFoundException() {
        super(BaseResponseStatus.PRODUCT_NOT_FOUND);
    }
}

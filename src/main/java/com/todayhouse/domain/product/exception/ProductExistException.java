package com.todayhouse.domain.product.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class ProductExistException extends BaseException {
    public ProductExistException() {
        super(BaseResponseStatus.PRODUCT_EXIST);
    }
}

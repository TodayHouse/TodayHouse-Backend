package com.todayhouse.domain.product.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class SellerNotSettingException extends BaseException {
    public SellerNotSettingException() {
        super(BaseResponseStatus.SELLER_NOT_SETTING);
    }
}

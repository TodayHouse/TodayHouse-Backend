package com.todayhouse.domain.product.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class StockNotEnoughException extends BaseException {
    public StockNotEnoughException() {
        super(BaseResponseStatus.STOCK_NOT_ENOUGH);
    }
}

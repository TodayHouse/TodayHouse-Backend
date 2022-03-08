package com.todayhouse.domain.product.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class SelectionOptionNotFoundException extends BaseException {
    public SelectionOptionNotFoundException() {
        super(BaseResponseStatus.SELECTION_OPTION_NOT_FOUND);
    }
}

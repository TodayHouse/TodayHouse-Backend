package com.todayhouse.domain.scrap.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class ScrapNotFoundException extends BaseException {
    public ScrapNotFoundException() {
        super(BaseResponseStatus.SCRAP_NOT_FOUND);
    }
}

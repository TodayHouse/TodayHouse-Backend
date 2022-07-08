package com.todayhouse.domain.inquiry.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class InquiryNotFoundException extends BaseException {
    public InquiryNotFoundException() {
        super(BaseResponseStatus.INQUIRY_NOT_FOUND);
    }
}

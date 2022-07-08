package com.todayhouse.domain.inquiry.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class InvalidInquiryDeleteException extends BaseException {
    public InvalidInquiryDeleteException() {
        super(BaseResponseStatus.INQUIRY_CANNOT_DELETE);
    }
}

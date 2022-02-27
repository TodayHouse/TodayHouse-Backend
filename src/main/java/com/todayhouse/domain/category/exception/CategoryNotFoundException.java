package com.todayhouse.domain.category.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class CategoryNotFoundException extends BaseException {
    public CategoryNotFoundException() {
        super(BaseResponseStatus.CATEGORY_NOT_FOUND);
    }
}

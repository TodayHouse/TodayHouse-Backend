package com.todayhouse.domain.category.exception;

import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;

public class CategoryExistException extends BaseException {
    public CategoryExistException() {
        super(BaseResponseStatus.SAME_CATEGORY_EXIST);
    }
}

package com.todayhouse.security.domian.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class BaseResponse {
    Boolean isSuccess;
    int code;
    String message;
    Object result;
}

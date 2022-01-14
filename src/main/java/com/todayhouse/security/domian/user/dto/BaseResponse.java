package com.todayhouse.security.domian.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Builder
@Getter
public class BaseResponse {
    Boolean isSuccess;
    int code;
    String message;
    Object result;
}

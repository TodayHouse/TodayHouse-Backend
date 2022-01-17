package com.todayhouse.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.todayhouse.global.error.BaseResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.todayhouse.global.error.BaseResponseStatus.SUCCESS;

@Getter
// AllArgsConstructor : 모든 필드값을 받는 생성자 생성
@AllArgsConstructor
// @JsonPropertyOrder : json serialization 순서를 정의
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> { // 모든 return은 BaseResponse 포맷으로 전달되며, success / error 를 모두 다룹니다.

    private final Boolean isSuccess;
    private final String message;
    private final int code;
    @JsonInclude(JsonInclude.Include.NON_NULL) // 결과값이 공백일 경우 json에 포함하지 않도록
    private T result;

    // 요청에 성공한 경우
    public BaseResponse(T result){
        this.isSuccess = SUCCESS.isSuccess();
        this.message = SUCCESS.getMessage();
        this.code = SUCCESS.getCode();
        this.result = result;
    }

    // 요청에 실패한 경우
    public BaseResponse(BaseResponseStatus status) {
        this.isSuccess = status.isSuccess();
        this.message = status.getMessage();
        this.code = status.getCode();
    }
}

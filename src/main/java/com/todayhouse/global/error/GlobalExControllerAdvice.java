package com.todayhouse.global.error;

import com.todayhouse.global.common.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
// 모든 RestController에 적용
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExControllerAdvice {

    // 내부 exception, 최하단에 위치
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public BaseResponse baseException(Exception e){
        log.error("Exception : {}",BaseResponseStatus.OTHERS.getMessage(), e);
        return new BaseResponse(BaseResponseStatus.OTHERS);
    }
}

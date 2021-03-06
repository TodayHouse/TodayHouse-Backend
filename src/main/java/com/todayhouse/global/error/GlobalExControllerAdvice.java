package com.todayhouse.global.error;

import com.todayhouse.global.common.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.io.IOException;

@Slf4j
// 모든 RestController에 적용
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExControllerAdvice {

    // @Validated 검증 실패
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResponse handleConstraintViolationException(ConstraintViolationException e) throws IOException {
        log.error("Exception : {}", e.getMessage());
        return new BaseResponse(BaseResponseStatus.VALID_EXCEPTION, e.getMessage());
    }

    // @Valid 검증 실패
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse InvalidArgumentValidResponse(MethodArgumentNotValidException e) {
        log.error("Exception : {}, 입력값 : {}", e.getBindingResult().getFieldError(), e.getBindingResult().getFieldError());
        return new BaseResponse(BaseResponseStatus.VALID_EXCEPTION, e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public BaseResponse InvalidArgumentBindResponse(BindException e) {
        log.error("Exception : {}, 입력값 : {}", e.getBindingResult().getFieldError(), e.getBindingResult().getFieldError());
        return new BaseResponse(BaseResponseStatus.VALID_EXCEPTION, e.getBindingResult().getFieldError().getDefaultMessage());
    }


    // BaseException return
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BaseException.class)
    public BaseResponse baseResponse(BaseException e) {
        log.error("{} Exception {}: {}", e.getStatus(), e.getStatus().getCode(), e.getStatus().getMessage());
        return new BaseResponse(e.getStatus());
    }

    // 내부 exception, 최하단에 위치
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public BaseResponse baseException(Exception e) {
        log.error("Exception : {}", BaseResponseStatus.OTHERS.getMessage(), e);
        return new BaseResponse(BaseResponseStatus.OTHERS);
    }
}

package com.todayhouse.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BaseException extends RuntimeException {
    // Exception 클래스를 상속
    // Service layer 에서 Exception 발생 시 BaseResponseStatus 객체의 적절한 에러코드를 status 로 받은 후,
    // Controller 로 전달되어 BaseResponse 에 매핑되어 json 으로 전달됩니다.
    private BaseResponseStatus status;
}

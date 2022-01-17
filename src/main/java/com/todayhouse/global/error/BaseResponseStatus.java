package com.todayhouse.global.error;

import lombok.Getter;

// 모든 에러 코드, 메시지를 관리합니다. 필요한 에러 코드와 메시지가 있다면 편하게 추가해주시기 바랍니다.
@Getter
public enum BaseResponseStatus {
    // 요청 성공
    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    // 2000 : Request 값 오류
    // user
    EMPTY_JWT(false, 2000, "JWT가 존재하지 않습니다."),
    INVALID_JWT(false, 2001, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2002,"권한이 없는 회원의 접근입니다."),
    POST_USER_EMPTY_EMAIL(false, 2003, "이메일을 입력해주세요."),
    POST_USER_INVALID_EMAIL(false, 2004, "이메일 형식을 확인해주세요."),
    POST_USER_EXISTS_EMAIL(false,2005,"중복된 이메일입니다."),
    POST_USER_EMPTY_PASSWORD(false,2006, "비밀번호를 입력해주세요."),
    POST_USER_INVALID_PASSWORD(false,2007, "비밀번호 형식을 확인해주세요."),
    POST_USER_EMPTY_BIRTH(false, 2008, "생년월일을 입력해주세요."),
    POST_USER_EMPTY_NICKNAME(false, 2009, "닉네임을 입력해주세요."),
    POST_USER_EXISTS_NICKNAME(false,2010,"중복된 닉네임입니다."),

    // product
    POST_PRODUCT_EMPTY_BRAND(false, 2100, "브랜드명을 입력해주세요."),
    POST_PRODUCT_EMPTY_TITLE(false, 2101, "제목을 입력해주세요."),
    POST_PRODUCT_EMPTY_PRICE(false, 2102, "가격을 입력해주세요."),
    POST_PRODUCT_EMPTY_DELIVERY(false, 2103, "배송비를 입력해주세요."),

    // story
    POST_STORY_EMPTY_TITLE(false, 2200, "제목을 입력해주세요."),
    POST_STORY_EMPTY_CONTENT(false, 2201, "본문을 입력해주세요."),
    POST_STORY_EMPTY_CATEGORY(false, 2202, "카테고리를 선택해주세요."),

    // 3000 : Database / Server 오류
    // common
    DATABASE_ERROR(false, 3000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 3001, "서버 연결에 실패하였습니다."),

    // user
    POST_USER_FAIL_JOIN(false, 3002, "회원가입에 실패하였습니다."),
    POST_USER_FAIL_LOGIN(false, 3003, "로그인에 실패하였습니다.");

    // product

    // story
    


    // 4000, 5000...추가적인 에러코드 필요 시 자유롭게 작성해주세요.

    private final boolean isSuccess;
    private final int code;
    private final String message;

    // BaseResponse 에서 각 해당하는 코드, 메시지를 생성자로 매핑해줍니다.
    private BaseResponseStatus(boolean isSuccess, int code, String message){
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}

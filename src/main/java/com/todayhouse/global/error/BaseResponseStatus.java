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
    INVALID_USER_JWT(false, 2002, "권한이 없는 회원의 접근입니다."),
    POST_USER_EMPTY_EMAIL(false, 2003, "이메일을 입력해주세요."),
    POST_USER_INVALID_EMAIL(false, 2004, "이메일 형식을 확인해주세요."),
    POST_USER_EXISTS_EMAIL(false, 2005, "중복된 이메일입니다."),
    POST_USER_EMPTY_PASSWORD(false, 2006, "비밀번호를 입력해주세요."),
    POST_USER_INVALID_PASSWORD(false, 2007, "비밀번호 형식을 확인해주세요."),
    POST_USER_EMPTY_BIRTH(false, 2008, "생년월일을 입력해주세요."),
    POST_USER_EMPTY_NICKNAME(false, 2009, "닉네임을 입력해주세요."),
    POST_USER_EXISTS_NICKNAME(false, 2010, "중복된 닉네임입니다."),
    USER_NOT_FOUND(false, 2011, "존재하지 않는 유저입니다."),
    WRONG_PASSWORD(false, 2012, "잘못된 비밀번호입니다."),
    WRONG_SIGNUP_PASSWORD(false, 2013, "비밀번호 확인이 일치하지 않습니다."),
    INVALID_AUTH_EMAIL(false, 2014, "인증받지 않은 이메일입니다."),
    INVALID_GUEST_EMAIL(false, 2015, ""),
    INVALID_EMAIL_TOKEN(false, 2016, "유효하지 않은 코드입니다."),
    SELLER_EXIST(false, 2017, "이미 입점한 유저입니다."),
    SELLER_NOT_FOUND(false, 2018, "입점을 하지 않았습니다."),

    // product
    POST_PRODUCT_EMPTY_DELIVERY(false, 2100, "배송비를 입력해주세요."),
    PRODUCT_NOT_FOUND(false, 2101, "상품을 찾을 수 없습니다."),
    SELLER_NOT_SETTING(false, 2102, "Product에 Seller가 설정되지 않았습니다."),
    PRODUCT_IMAGE_FIXED(false, 2103, "설정된 image는 변경할 수 없습니다."),
    PRODUCT_EXIST(false, 2104, "상품을 이미 설정했습니다."),
    STOCK_NOT_ENOUGH(false, 2105, "상품 수량이 0보다 작을 수 없습니다."),

    // story
    POST_STORY_EMPTY_TITLE(false, 2200, "제목을 입력해주세요."),
    POST_STORY_EMPTY_CONTENT(false, 2201, "본문을 입력해주세요."),
    POST_STORY_EMPTY_CATEGORY(false, 2202, "카테고리를 선택해주세요."),

    // jwt
    NOT_GUEST_ACCESS(false, 2300, "이미 회원가입 했습니다."),
    IS_GUEST_ACCESS(false, 2301, "회원가입을 하지 않았습니다."),
    INVALID_REDIRECT_URI(false, 2302, "허용되지 않은 URI입니다."),
    INVALID_AUTH(false, 2303, "올바르지 않은 권한입니다."),
    INVALID_REQUEST(false, 2304, "올바르지 않은 요청입니다."),

    // category
    SAME_CATEGORY_EXIST(false, 2404, "같은 카테고리가 존재합니다."),
    CATEGORY_NOT_FOUND(false, 2405, "해당 카테고리를 찾을 수 없습니다. 부모 카테고리가 없다면 parentId를 지워주세요"),

    // option
    PARENT_OPTION_EXIST(false, 2406, "상위 옵션을 이미 설정했습니다."),
    PARENT_OPTION_NOT_FOUND(false, 2407, "상위 옵션을 찾을 수 없습니다."),
    CHILD_OPTION_NOT_FOUND(false, 2408, "하위 옵션을 찾을 수 없습니다."),
    SELECTION_OPTION_NOT_FOUND(false, 2409, "선택 옵션을 찾을 수 없습니다."),

    // order
    ORDER_NOT_FOUND(false, 2500, "주문을 찾을 수 없습니다."),

    // 3000 : Database / Server 오류
    // common
    DATABASE_ERROR(false, 3000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 3001, "서버 연결에 실패하였습니다."),

    // user
    POST_USER_FAIL_JOIN(false, 3002, "회원가입에 실패하였습니다."),
    POST_USER_FAIL_LOGIN(false, 3003, "로그인에 실패하였습니다."),

    // product

    // story
    NOT_FOUND_STORY(false, 3300, "해당하는 스토리를 찾을 수 없습니다."),

    // image
    NOT_FOUND_IMAGE(false, 3400, "해당하는 이미지를 찾을 수 없습니다."),

    // 4000 : AWS S3 관련 오류
    IMAGE_FILE_IO_EXCEPTION(false, 4000, "이미지파일을 다루는 과정에서 오류가 발생하였습니다."),
    AMAZON_CLIENT_EXCEPTION(false, 4001, "아마존 서버에 업로드하는 과정에서 오류가 발생했습니다."),
    INVALID_FILE_EXTENSION_EXCEPTION(false, 4002, "잘못된 형식의 파일입니다."),

    // 그 밖의
    OTHERS(false, 9999, "내부 오류가 발생했습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    // BaseResponse 에서 각 해당하는 코드, 메시지를 생성자로 매핑해줍니다.
    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}

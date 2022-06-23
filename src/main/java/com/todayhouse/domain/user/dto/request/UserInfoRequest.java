package com.todayhouse.domain.user.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserInfoRequest {
    String email;
    String birth;
    @Pattern(regexp = "^m|f|$", message = "남자는 m, 여지는 f로 입력해주세요.")
    String gender;
    String nickname;
    String introduction;
}

package com.todayhouse.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequest {
    @Size(max = 50)
    @Email(message = "이메일 형식에 맞지 않습니다.")
    String email;

    @Pattern(regexp = "^[A-Za-z[0-9]]{8,50}$",
            message = "비밀번호는 영문, 숫자를 포함하여 8자 이상이어야 합니다.")
    String password;
}

package com.todayhouse.domain.email.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailSendRequest {
    @Size(max = 50)
    @Email(message = "이메일 형식에 맞지 않습니다.")
    String email;
}

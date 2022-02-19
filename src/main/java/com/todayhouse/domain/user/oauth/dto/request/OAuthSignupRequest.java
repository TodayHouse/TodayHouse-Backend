package com.todayhouse.domain.user.oauth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OAuthSignupRequest {
    @Size(max = 50)
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotBlank
    @Size(min = 2, max = 15)
    private String nickname;

    @AssertTrue
    private boolean agreeAge;

    @AssertTrue
    private boolean agreeTOS;

    @AssertTrue
    private boolean agreePICU;

    @NotNull
    private boolean agreePromotion;
}

package com.todayhouse.domain.user.oauth.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OAuthSignupInfoResponse {
    private String email;
}
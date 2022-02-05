package com.todayhouse.domain.user.oauth.dto.response;

import com.todayhouse.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthSignupResponse {
    private String email;
    private String nickname;
    private boolean agreeAge;
    private boolean agreeTOS;
    private boolean agreePICU;
    private boolean agreePromotion;

    public OAuthSignupResponse(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.agreeAge = user.isAgreeAge();
        this.agreeTOS = user.isAgreeTOS();
        this.agreePICU = user.isAgreePICU();
        this.agreePromotion = user.isAgreePromotion();
    }
}

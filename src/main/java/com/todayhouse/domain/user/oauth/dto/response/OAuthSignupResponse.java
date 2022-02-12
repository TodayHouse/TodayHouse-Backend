package com.todayhouse.domain.user.oauth.dto.response;

import com.todayhouse.domain.user.domain.User;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
        this.agreeAge = user.getAgreement().isAgreeAge();
        this.agreeTOS = user.getAgreement().isAgreeTOS();
        this.agreePICU = user.getAgreement().isAgreePICU();
        this.agreePromotion = user.getAgreement().isAgreePromotion();
    }
}

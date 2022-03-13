package com.todayhouse.domain.user.dto.response;

import com.todayhouse.domain.user.domain.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserSignupResponse {
    private String email;
    private String nickname;
    private boolean agreeAge;
    private boolean agreeTOS;
    private boolean agreePICU;
    private boolean agreePromotion;

    public UserSignupResponse(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.agreeAge = user.getAgreement().isAgreeAge();
        this.agreeTOS = user.getAgreement().isAgreeTOS();
        this.agreePICU = user.getAgreement().isAgreePICU();
        this.agreePromotion = user.getAgreement().isAgreePromotion();
    }
}
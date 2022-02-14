package com.todayhouse.domain.user.dto.response;

import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFindResponse {
    private String email;
    private String nickname;
    private AuthProvider authProvider;
    private boolean agreeAge;
    private boolean agreeTOS;
    private boolean agreePICU;
    private boolean agreePromotion;

    public UserFindResponse(User user) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.authProvider = user.getAuthProvider();
        this.agreeAge = user.getAgreement().isAgreeAge();
        this.agreeTOS = user.getAgreement().isAgreeTOS();
        this.agreePICU = user.getAgreement().isAgreePICU();
        this.agreePromotion = user.getAgreement().isAgreePromotion();
    }
}

package com.todayhouse.domain.user.dto.response;

import com.todayhouse.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignupResponse {
    private String email;
    private String nickname;
    private boolean agreeAge;
    private boolean agreeTOS;
    private boolean agreePICU;
    private boolean agreePromotion;

    public UserSignupResponse(User user){
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.agreeAge = user.isAgreeAge();
        this.agreeTOS = user.isAgreeTOS();
        this.agreePICU = user.isAgreePICU();
        this.agreePromotion = user.isAgreePromotion();
    }
}
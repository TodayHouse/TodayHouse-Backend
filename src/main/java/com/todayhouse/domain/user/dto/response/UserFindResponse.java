package com.todayhouse.domain.user.dto.response;

import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFindResponse {
    private Long id;
    private String email;
    private String nickname;
    private AuthProvider authProvider;
    private String birth;
    private String gender;
    private String profileImage;
    private String introduction;
    private boolean agreeAge;
    private boolean agreeTOS;
    private boolean agreePICU;
    private boolean agreePromotion;

    public UserFindResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.authProvider = user.getAuthProvider();
        this.birth = user.getBirth();
        this.gender = user.getGender();
        this.profileImage = user.getProfileImage();
        this.introduction = user.getIntroduction();
        this.agreeAge = user.getAgreement().isAgreeAge();
        this.agreeTOS = user.getAgreement().isAgreeTOS();
        this.agreePICU = user.getAgreement().isAgreePICU();
        this.agreePromotion = user.getAgreement().isAgreePromotion();
    }
}

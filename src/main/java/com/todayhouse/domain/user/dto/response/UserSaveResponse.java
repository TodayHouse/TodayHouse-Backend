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
public class UserSaveResponse {
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private List<String> role;
    private boolean agreeTOS;
    private boolean agreePICU;
    private boolean agreePromotion;

    public UserSaveResponse (User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        this.role = user.getRoles();
        this.agreeTOS = user.isAgreeTOS();
        this.agreePICU = user.isAgreePICU();
        this.agreePromotion = user.isAgreePromotion();
    }
}
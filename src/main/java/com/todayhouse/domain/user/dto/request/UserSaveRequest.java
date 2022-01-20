package com.todayhouse.domain.user.dto.request;

import com.todayhouse.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSaveRequest {
    private String email;
    private String password;
    private String nickname;
    private boolean agreeTOS;
    private boolean agreePICU;
    private boolean agreePromotion;
    private boolean push;

    public User toEntity(){
        return User.builder()
                .email(this.email)
                .password(new BCryptPasswordEncoder().encode(this.password))
                .roles(Collections.singletonList("ROLE_USER"))
                .nickname(this.nickname)
                .agreeTOS(this.agreeTOS)
                .agreePICU(this.agreePICU)
                .agreePromotion(this.agreePromotion)
                .build();
    }
}

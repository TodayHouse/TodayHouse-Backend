package com.todayhouse.domain.user.dto.request;

import com.todayhouse.domain.user.domain.Agreement;
import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignupRequest {
    @NotBlank
    @Size(max=50)
    private String email;

    @NotBlank
    @Size(min=8,max=200)
    private String password1;

    @NotBlank
    @Size(min=8,max=200)
    private String password2;

    @NotBlank
    @Size(min=2,max=15)
    private String nickname;

    @NotNull
    private boolean agreeAge;
    @NotNull
    private boolean agreeTOS;
    @NotNull
    private boolean agreePICU;
    @NotNull
    private boolean agreePromotion;

    public User toEntity(){
        Agreement agreement = Agreement.builder().agreeAge(agreeAge).agreeTOS(agreeTOS)
                .agreePICU(agreePICU).agreePromotion(agreePromotion).build();
        return User.builder()
                .authProvider(AuthProvider.LOCAL)
                .email(email)
                .password(new BCryptPasswordEncoder().encode(password1))
                .roles(Collections.singletonList(Role.USER))
                .nickname(nickname)
                .agreement(agreement)
                .build();
    }
}

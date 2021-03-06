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

import javax.validation.constraints.*;
import java.util.Collections;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignupRequest {
    @Size(max = 50)
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @Pattern(regexp = "^[A-Za-z[0-9]]{8,50}$",
            message = "비밀번호는 영문, 숫자를 포함하여 8자 이상이어야 합니다.")
    private String password1;

    @NotBlank
    @Size(min = 8, max = 200)
    private String password2;

    @NotBlank
    @Size(min = 2, max = 15)
    private String nickname;

    @AssertTrue
    private boolean agreeAge;

    @AssertTrue
    private boolean agreeTOS;

    @AssertTrue
    private boolean agreePICU;

    @NotNull
    private boolean agreePromotion;

    public User toEntity() {
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

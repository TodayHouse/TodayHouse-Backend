package com.todayhouse.domain.user.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@ToString
@Builder
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Agreement {
    @Column(name = "agree_age")
    private boolean agreeAge;

    @Column(name = "agree_tos")
    private boolean agreeTOS;

    @Column(name = "agree_picu")
    private boolean agreePICU;

    @Column(name = "agree_promotion")
    private boolean agreePromotion;

    // 모든 약관 동의
    public static Agreement agreeAll(){
        return Agreement.builder()
                .agreeAge(true)
                .agreeTOS(true)
                .agreePICU(true)
                .agreePromotion(true).build();
    }
}

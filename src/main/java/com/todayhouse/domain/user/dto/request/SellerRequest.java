package com.todayhouse.domain.user.dto.request;

import com.todayhouse.domain.user.domain.Seller;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SellerRequest {
    @Email
    private String email;

    @NotBlank
    private String companyName;

    @NotBlank
    private String representative;

    @NotBlank
    private String customerCenter;

    @NotNull
    private int registrationNum;

    public Seller toEntity() {
        return Seller.builder()
                .email(this.email)
                .companyName(this.companyName)
                .customerCenter(this.customerCenter)
                .representative(this.representative)
                .registrationNum(this.representative)
                .build();
    }
}

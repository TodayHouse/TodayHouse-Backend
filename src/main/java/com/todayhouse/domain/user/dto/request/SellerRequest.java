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
    @Email(message = "email을 입력해주세요")
    private String email;

    @NotBlank(message = "brand를 입력해주세요")
    private String brand;

    @NotBlank(message = "companyName을 입력해주세요")
    private String companyName;

    @NotBlank(message = "representative를 입력해주세요")
    private String representative;

    @NotBlank(message = "customerCenter를 입력해주세요")
    private String customerCenter;

    @NotNull(message = "registrationNum를 입력해주세요")
    private int registrationNum;

    public Seller toEntity() {
        return Seller.builder()
                .email(this.email)
                .brand(this.brand)
                .companyName(this.companyName)
                .customerCenter(this.customerCenter)
                .representative(this.representative)
                .registrationNum(this.representative)
                .build();
    }
}

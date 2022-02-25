package com.todayhouse.domain.user.dto.response;

import com.todayhouse.domain.user.domain.Seller;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerResponse {
    private Long id;
    private String email;
    private String brand;
    private String companyName;
    private String representative;
    private String customerCenter;
    private String registrationNum;

    public SellerResponse(Seller seller) {
        this.id = seller.getId();
        this.email = seller.getEmail();
        this.brand = seller.getBrand();
        this.companyName = seller.getCompanyName();
        this.representative = seller.getRepresentative();
        this.customerCenter = seller.getCustomerCenter();
        this.registrationNum = seller.getRegistrationNum();
    }

}

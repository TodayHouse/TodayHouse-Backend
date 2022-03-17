package com.todayhouse.domain.user.dto.response;

import com.todayhouse.domain.user.domain.Seller;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SellerResponse {
    private Long id;
    private String email;
    private String brand;
    private String companyName;
    private String representative;
    private String customerCenter;
    private String registrationNum;
    private String businessAddress;

    public SellerResponse(Seller seller) {
        this.id = seller.getId();
        this.email = seller.getEmail();
        this.brand = seller.getBrand();
        this.companyName = seller.getCompanyName();
        this.representative = seller.getRepresentative();
        this.customerCenter = seller.getCustomerCenter();
        this.registrationNum = seller.getRegistrationNum();
        this.businessAddress = seller.getBusinessAddress();
    }

}

package com.todayhouse.domain.inquiry.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquirySearchRequest {
    private Long productId;
    private Boolean isMyInquiry;

    public InquirySearchRequest(Long productId, Boolean isMyInquiry){
        this.productId = productId;
        this.isMyInquiry = isMyInquiry;
    }
}

package com.todayhouse.domain.product.dto.request;

import lombok.*;

// 해당 변수로 product 정렬
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductSearchRequest {
    private String brand;
    private String search;
    private String categoryName;
    private Integer priceFrom;
    private Integer priceTo;
    private Boolean deliveryFee;
    private Boolean specialPrice;
}

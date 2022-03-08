package com.todayhouse.domain.product.dto.request;

import lombok.*;

// 해당 변수로 product 정렬
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductSearchRequest {
    private String brand;
    private Long categoryId;
    private Integer priceFrom;
    private Integer priceTo;
    private boolean deliveryFee;
    private boolean specialPrice;
}

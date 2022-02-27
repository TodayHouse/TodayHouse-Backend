package com.todayhouse.domain.product.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductUpdateRequest {
    @NotNull
    private Long id;
    private String title;
    private String image;
    private int price;
    private int discountRate;
    private int deliveryFee;
    private boolean specialPrice;
    private String productDetail;
    private int sales;
}
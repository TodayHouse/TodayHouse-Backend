package com.todayhouse.domain.product.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductUpdateRequest {
    @NotNull(message = "id를 입력해주세요.")
    private Long id;
    private Long categoryId;
    private String title;
    private String image;
    private String productDetail;
    private int price;
    private int sales;
    private int deliveryFee;
    private int discountRate;
    private boolean specialPrice;
}

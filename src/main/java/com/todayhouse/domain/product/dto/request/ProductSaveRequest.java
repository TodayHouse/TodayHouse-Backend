package com.todayhouse.domain.product.dto.request;

import com.sun.istack.NotNull;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.user.domain.Seller;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductSaveRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String image;

    @NotNull
    private int price;

    @NotNull
    private int discountRate;

    @NotNull
    private int deliveryFee;

    @NotNull
    private boolean specialPrice;

    private String productDetail;

    public Product toEntity(Seller seller) {
        return Product.builder()
                .title(this.title)
                .image(this.image)
                .price(this.price)
                .discountRate(this.discountRate)
                .deliveryFee(this.deliveryFee)
                .specialPrice(this.specialPrice)
                .productDetail(this.productDetail)
                .sales(0)
                .seller(seller).build();
    }
}

package com.todayhouse.domain.product.dto.request;

import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.user.domain.Seller;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductSaveRequest {
    private String title;
    private String image;
    private int price;
    private int discountRate;
    private int deliveryFee;
    private boolean specialPrice;
    private String productDetail;
    private int sales;

    public Product toEntity(Seller seller){
        return Product.builder()
                .title(this.title)
                .image(this.image)
                .price(this.price)
                .discountRate(this.discountRate)
                .deliveryFee(this.deliveryFee)
                .specialPrice(this.specialPrice)
                .productDetail(this.productDetail)
                .sales(this.sales)
                .seller(seller).build();
    }
}

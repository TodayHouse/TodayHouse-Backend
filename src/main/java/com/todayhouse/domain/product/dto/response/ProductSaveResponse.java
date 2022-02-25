package com.todayhouse.domain.product.dto.response;

import com.todayhouse.domain.product.domain.Product;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductSaveResponse {
    private Long id;
    private String title;
    private String image;
    private int price;
    private int deliveryFee;
    private int discountRate;
    private boolean specialPrice;
    private String productDetail;
    private int sales;

    public ProductSaveResponse(Product product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.image = product.getImage();
        this.price = product.getPrice();
        this.discountRate = product.getDiscountRate();
        this.deliveryFee = product.getDeliveryFee();
        this.specialPrice = product.isSpecialPrice();
        this.productDetail = product.getProductDetail();
        this.sales = product.getSales();
    }
}

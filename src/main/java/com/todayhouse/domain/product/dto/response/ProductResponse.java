package com.todayhouse.domain.product.dto.response;

import com.todayhouse.domain.product.domain.Product;
import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String brand;
    private String title;
    private String image;
    private String productDetail;
    private int sales;
    private int price;
    private int deliveryFee;
    private int discountRate;
    private boolean specialPrice;
    private List<String> images;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.brand = product.getBrand();
        this.title = product.getTitle();
        this.image = product.getImage();
        this.price = product.getPrice();
        this.deliveryFee = product.getDeliveryFee();
        this.discountRate = product.getDiscountRate();
        this.specialPrice = product.isSpecialPrice();
        this.productDetail = product.getProductDetail();
        this.sales = product.getSales();
        this.images = Optional.ofNullable(product.getImages())
                .orElseGet(Collections::emptyList).stream().filter(Objects::nonNull)
                .map(productImage -> productImage.getFileName()).collect(Collectors.toList());
    }
}

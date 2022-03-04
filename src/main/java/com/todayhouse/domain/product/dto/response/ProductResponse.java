package com.todayhouse.domain.product.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.todayhouse.domain.product.domain.Product;
import lombok.*;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private Long sellerId;
    private String brand;
    private String title;
    private String image;
    private String option1;
    private String option2;
    private String selectionOption;
    private String productDetail;
    private int price;
    private int sales;
    private int deliveryFee;
    private int discountRate;
    private boolean specialPrice;
    private Set<ParentOptionResponse> options;
    private Set<SelectionOptionResponse> selectionOptions;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.sellerId = product.getId();
        this.brand = product.getBrand();
        this.title = product.getTitle();
        this.image = product.getImage();
        this.option1 = product.getOption1();
        this.option2 = product.getOption2();
        this.selectionOption = product.getSelectionOption();
        this.productDetail = product.getProductDetail();
        this.price = product.getPrice();
        this.sales = product.getSales();
        this.deliveryFee = product.getDeliveryFee();
        this.discountRate = product.getDiscountRate();
        this.specialPrice = product.isSpecialPrice();

        this.options = Optional.ofNullable(product.getOptions())
                .orElseGet(Collections::emptySet).stream().filter(Objects::nonNull)
                .map(parentOption -> new ParentOptionResponse(parentOption)).collect(Collectors.toSet());

        this.selectionOptions = Optional.ofNullable(product.getSelectionOptions())
                .orElseGet(Collections::emptySet).stream().filter(Objects::nonNull)
                .map(selectionOption -> new SelectionOptionResponse(selectionOption)).collect(Collectors.toSet());
    }


}

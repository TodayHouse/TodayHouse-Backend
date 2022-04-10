package com.todayhouse.domain.product.dto.request;

import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.user.domain.Seller;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductSaveRequest {
    @NotBlank(message = "title을 입력해주세요")
    private String title;

    @NotNull(message = "price를 입력해주세요")
    private int price;

    @NotNull(message = "discountRate를 입력해주세요")
    private int discountRate;

    @NotNull(message = "deliveryFee를 입력해주세요")
    private int deliveryFee;

    @NotNull(message = "specialPrice를 입력해주세요")
    private boolean specialPrice;

    @NotNull(message = "categoryId를 입력해주세요")
    private String categoryName;

    private String productDetail;

    private String parentOption;

    private String childOption;

    private String selectionOption;

    private Set<ProductParentOptionSaveRequest> parentOptions;

    private Set<ProductSelectionOptionSaveRequest> selectionOptions;

    // ChildOption 까지 entity로 변환
    public Product toEntityWithParentAndSelection(Seller seller, Category category, String image) {
        Product product = Product.builder()
                .title(this.title)
                .price(this.price)
                .discountRate(this.discountRate)
                .deliveryFee(this.deliveryFee)
                .specialPrice(this.specialPrice)
                .productDetail(this.productDetail)
                .sales(0)
                .image(image)
                .seller(seller)
                .category(category)
                .parentOption(this.parentOption)
                .childOption(this.childOption)
                .selectionOption(this.selectionOption)
                .build();

        Optional.ofNullable(parentOptions)
                .orElseGet(Collections::emptySet).stream().filter(Objects::nonNull)
                .map(parentRequest -> parentRequest.toEntityWithChild(product)).collect(Collectors.toSet());

        Optional.ofNullable(selectionOptions)
                .orElseGet(Collections::emptySet).stream().filter(Objects::nonNull)
                .map(selectionRequest -> selectionRequest.toEntity(product)).collect(Collectors.toSet());
        return product;
    }
}

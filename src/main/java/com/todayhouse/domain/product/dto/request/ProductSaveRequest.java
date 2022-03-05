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

    @NotBlank(message = "image를 입력해주세요")
    private String image;

    private int price;

    @NotNull(message = "discountRate를 입력해주세요")
    private int discountRate;

    @NotNull(message = "deliveryFee를 입력해주세요")
    private int deliveryFee;

    @NotNull(message = "specialPrice를 입력해주세요")
    private boolean specialPrice;

    @NotNull(message = "categoryId를 입력해주세요")
    private Long categoryId;

    private String productDetail;

    private String option;

    private String childOption;

    private String selectionOption;

    private Set<ParentOptionSaveRequest> options;

    private Set<SelectionOptionSaveRequest> selectionOptions;

    // ChildOption 까지 entity로 변환
    public Product toEntityWithParentAndSelection(Seller seller, Category category) {
        Product product = Product.builder()
                .title(this.title)
                .image(this.image)
                .price(this.price)
                .discountRate(this.discountRate)
                .deliveryFee(this.deliveryFee)
                .specialPrice(this.specialPrice)
                .productDetail(this.productDetail)
                .sales(0)
                .seller(seller)
                .category(category)
                .option1(this.option)
                .option2(this.childOption)
                .selectionOption(this.selectionOption)
                .build();

        Optional.ofNullable(options)
                .orElseGet(Collections::emptySet).stream().filter(Objects::nonNull)
                .map(parentRequest -> parentRequest.toEntityWithChild(product)).collect(Collectors.toSet());

        Optional.ofNullable(selectionOptions)
                .orElseGet(Collections::emptySet).stream().filter(Objects::nonNull)
                .map(selectionRequest -> selectionRequest.toEntity(product)).collect(Collectors.toSet());
        return product;
    }
}

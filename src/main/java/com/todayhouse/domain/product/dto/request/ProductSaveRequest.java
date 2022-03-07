package com.todayhouse.domain.product.dto.request;

import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.user.domain.Seller;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
    private Long categoryId;

    private String productDetail;

    public Product toEntity(Seller seller, Category category, String image) {
        return Product.builder()
                .title(this.title)
                .price(this.price)
                .discountRate(this.discountRate)
                .deliveryFee(this.deliveryFee)
                .specialPrice(this.specialPrice)
                .productDetail(this.productDetail)
                .sales(0)
                .image(image)
                .seller(seller)
                .category(category).build();
    }
}

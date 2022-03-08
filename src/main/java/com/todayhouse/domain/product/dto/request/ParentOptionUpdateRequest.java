package com.todayhouse.domain.product.dto.request;

import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
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
public class ParentOptionUpdateRequest {
    @NotNull(message = "id를 입력해주세요.")
    private Long id;

    @NotNull(message = "price를 입력해주세요.")
    private int price;

    @NotNull(message = "stock을 입력해주세요.")
    private int stock;

    @NotBlank(message = "content를 입력해주세요.")
    private String content;

    public ParentOption toEntity (Product product) {
        return ParentOption.builder()
                .price(this.price)
                .stock(this.stock)
                .content(this.content)
                .product(product)
                .build();
    }
}

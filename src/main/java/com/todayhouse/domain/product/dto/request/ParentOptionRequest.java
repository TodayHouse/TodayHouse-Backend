package com.todayhouse.domain.product.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ParentOptionRequest {
    @NotNull
    private int price;

    @NotNull
    private int stock;

    @NotBlank
    private String content;

    private Set<ChildOptionRequest> childOptionRequests;

    public ParentOption toEntity(Product product) {
        ParentOption parent = ParentOption.builder()
                .price(this.price)
                .stock(this.stock)
                .content(this.content)
                .product(product)
                .build();

        Optional.ofNullable(childOptionRequests)
                .orElseGet(Collections::emptySet).stream().filter(Objects::nonNull)
                .forEach(childRequest -> childRequest.toEntity(parent));
        return parent;
    }
}

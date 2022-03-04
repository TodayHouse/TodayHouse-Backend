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
public class ParentOptionRequest {
    @NotNull
    private int price;

    @NotNull
    private int stock;

    @NotBlank
    private String content;

    private Set<ChildOptionRequest> childOptionRequests;

    public ParentOption toEntityWithChild(Product product) {
        ParentOption parent = ParentOption.builder()
                .price(this.price)
                .stock(this.stock)
                .content(this.content)
                .product(product)
                .build();

        Optional.ofNullable(childOptionRequests)
                .orElseGet(Collections::emptySet).stream().filter(Objects::nonNull)
                .map(childRequest -> childRequest.toEntity(parent)).collect(Collectors.toSet());
        return parent;
    }
}

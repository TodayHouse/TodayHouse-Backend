package com.todayhouse.domain.product.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.todayhouse.domain.product.domain.ParentOption;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ParentOptionResponse {
    private Long id;
    private int price;
    private int stock;
    private String content;
    private Set<ChildOptionResponse> childOptions;

    // childOption을 변환하여 보낼 것인지 경정 가능
    public ParentOptionResponse(ParentOption parentOption, boolean withChildren) {
        this.id = parentOption.getId();
        this.price = parentOption.getPrice();
        this.stock = parentOption.getStock();
        this.content = parentOption.getContent();

        if (withChildren)
            this.childOptions = Optional.ofNullable(parentOption.getChildren())
                    .orElseGet(Collections::emptySet).stream().filter(Objects::nonNull)
                    .map(childOption -> new ChildOptionResponse(childOption)).collect(Collectors.toSet());
    }
}

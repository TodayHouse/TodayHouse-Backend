package com.todayhouse.domain.product.dto.request;

import com.todayhouse.domain.product.domain.ChildOption;
import com.todayhouse.domain.product.domain.ParentOption;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChildOptionRequest {
    @NotNull
    private int price;

    @NotNull
    private int stock;

    @NotBlank
    private String content;

    public ChildOption toEntity(ParentOption parentOption) {
        return ChildOption.builder()
                .price(this.price)
                .stock(this.stock)
                .content(this.content)
                .parent(parentOption)
                .build();
    }
}

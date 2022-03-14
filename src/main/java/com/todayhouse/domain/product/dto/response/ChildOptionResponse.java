package com.todayhouse.domain.product.dto.response;

import com.todayhouse.domain.product.domain.ChildOption;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChildOptionResponse {
    private Long id;
    private int price;
    private int stock;
    private String content;

    public ChildOptionResponse(ChildOption childOption) {
        this.id = childOption.getId();
        this.price = childOption.getPrice();
        this.stock = childOption.getStock();
        this.content = childOption.getContent();
    }
}

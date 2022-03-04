package com.todayhouse.domain.product.dto.response;

import com.todayhouse.domain.product.domain.SelectionOption;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SelectionOptionResponse {
    private Long id;
    private int price;
    private int stock;
    private String content;

    public SelectionOptionResponse(SelectionOption selectionOption) {
        this.id = selectionOption.getId();
        this.price = selectionOption.getPrice();
        this.stock = selectionOption.getStock();
        this.content = selectionOption.getContent();
    }
}

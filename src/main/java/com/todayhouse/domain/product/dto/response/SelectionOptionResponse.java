package com.todayhouse.domain.product.dto.response;

import com.todayhouse.domain.product.domain.SelectionOption;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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

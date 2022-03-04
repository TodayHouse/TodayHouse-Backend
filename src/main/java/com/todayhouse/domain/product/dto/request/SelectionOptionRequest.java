package com.todayhouse.domain.product.dto.request;

import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.domain.SelectionOption;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SelectionOptionRequest {
    @NotNull(message = "가격을 입력해주세요. selectionOptions 불필요시 selectionOptions를 제거해주세요.")
    private int price;

    @NotNull(message = "stock을 입력해주세요. selectionOptions 불필요시 selectionOptions를 제거해주세요.")
    private int stock;

    @NotBlank(message = "content를 입력해주세요. selectionOptions 불필요시 selectionOptions를 제거해주세요.")
    private String content;

    public SelectionOption toEntity(Product product){
        return SelectionOption.builder()
                .price(this.price)
                .stock(this.stock)
                .content(this.content)
                .product(product).build();
    }
}

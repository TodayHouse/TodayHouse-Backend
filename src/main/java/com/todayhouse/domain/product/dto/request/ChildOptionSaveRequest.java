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
public class ChildOptionSaveRequest {
    @NotNull(message = "price를 입력해주세요. childOptions 불필요시 childOptions를 제거해주세요.")
    private int price;

    @NotNull(message = "stock을 입력해주세요. childOptions 불필요시 childOptions를 제거해주세요.")
    private int stock;

    @NotBlank(message = "content를 입력해주세요. childOptions 불필요시 childOptions를 제거해주세요.")
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

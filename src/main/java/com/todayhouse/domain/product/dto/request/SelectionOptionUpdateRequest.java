package com.todayhouse.domain.product.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SelectionOptionUpdateRequest {
    @NotNull(message = "id를 입력해주세요.")
    private Long id;

    @NotNull(message = "가격을 입력해주세요. selectionOptions 불필요시 selectionOptions를 제거해주세요.")
    private int price;

    @NotNull(message = "stock을 입력해주세요. selectionOptions 불필요시 selectionOptions를 제거해주세요.")
    private int stock;

    @NotBlank(message = "content를 입력해주세요. selectionOptions 불필요시 selectionOptions를 제거해주세요.")
    private String content;
}

package com.todayhouse.domain.product.dto.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SaveProductImagesRequest {
    @NotNull(message = "product id를 입력해주세요.")
    private Long productId;
}

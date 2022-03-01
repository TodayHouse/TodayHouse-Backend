package com.todayhouse.domain.product.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OptionRequest {
    @NotBlank
    private String content;
    @NotNull
    private int price;
    private int stock;
}

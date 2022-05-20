package com.todayhouse.domain.review.domain;

import lombok.*;

import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Rating {

    @NotNull(message = "total을 입력해주세요")
    @Max(value = 5, message = "1~5점 사이로 입력해주세요")
    @Min(value = 1, message = "1~5점 사이로 입력해주세요")
    private int total;

    @NotNull(message = "price를 입력해주세요")
    @Max(value = 5, message = "1~5점 사이로 입력해주세요")
    @Min(value = 1, message = "1~5점 사이로 입력해주세요")
    private int price;

    @NotNull(message = "design을 입력해주세요")
    @Max(value = 5, message = "1~5점 사이로 입력해주세요")
    @Min(value = 1, message = "1~5점 사이로 입력해주세요")
    private int design;

    @NotNull(message = "delivery를 입력해주세요")
    @Max(value = 5, message = "1~5점 사이로 입력해주세요")
    @Min(value = 1, message = "1~5점 사이로 입력해주세요")
    private int delivery;

    @NotNull(message = "durability를 입력해주세요")
    @Max(value = 5, message = "1~5점 사이로 입력해주세요")
    @Min(value = 1, message = "1~5점 사이로 입력해주세요")
    private int durability;
}

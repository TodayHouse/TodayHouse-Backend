package com.todayhouse.domain.review.dto.request;

import com.todayhouse.domain.review.domain.Review;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReviewSaveRequest {
    @NotNull(message = "rating을 입력해주세요")
    @Max(value = 5, message = "1~5점 사이로 입력해주세요")
    @Min(value = 1, message = "1~5점 사이로 입력해주세요")
    private int rating;

    @NotNull(message = "productId를 입력해주세요")
    private Long productId;

    @NotBlank(message = "내용을 입력해주세요")
    private String content;

    public Review toEntity() {
        return Review.builder()
                .rating(rating)
                .content(content)
                .build();
    }
}

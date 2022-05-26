package com.todayhouse.domain.review.dto.request;

import com.todayhouse.domain.review.domain.Rating;
import com.todayhouse.domain.review.domain.Review;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReviewSaveRequest {
    @Valid
    @NotNull(message = "Rating을 입력해주세요")
    private Rating rating;

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

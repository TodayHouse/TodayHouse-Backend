package com.todayhouse.domain.review.dto.request;

import lombok.*;

import javax.validation.constraints.Pattern;

@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReviewSearchRequest {
    private Long userId;
    private Long productId;
    @Pattern(regexp = "^[1-5](,[1-5](,[1-5])?(,[1-5])?(,[1-5])?)?",
            message = "1,2,3,4,5 형식으로 입력해주세요. 숫자는 1~5까지 입력 가능합니다.")
    private String ratings;
    private Boolean onlyImage;
}

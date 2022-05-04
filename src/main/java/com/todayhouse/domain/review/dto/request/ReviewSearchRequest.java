package com.todayhouse.domain.review.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReviewSearchRequest {
    private Integer rate;
    private Long userId;
    private Long productId;
    private Boolean isImage;
}

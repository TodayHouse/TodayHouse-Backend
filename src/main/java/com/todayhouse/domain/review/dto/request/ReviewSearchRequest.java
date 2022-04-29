package com.todayhouse.domain.review.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewSearchRequest {
    private Integer rate;
    private Long userId;
    private Boolean isImage;
}

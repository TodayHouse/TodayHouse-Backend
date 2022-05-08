package com.todayhouse.domain.review.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReviewSearchRequest {
    private Integer rate;
    private Long userId;
    private Long productId;
    private Boolean isImage;
}

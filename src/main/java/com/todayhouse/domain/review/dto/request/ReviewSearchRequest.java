package com.todayhouse.domain.review.dto.request;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReviewSearchRequest {
    private Integer rating;
    private Long userId;
    private Long productId;
    private Boolean isImage;
}

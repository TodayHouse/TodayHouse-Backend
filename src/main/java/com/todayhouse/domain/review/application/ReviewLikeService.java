package com.todayhouse.domain.review.application;

import com.todayhouse.domain.review.domain.ReviewLike;

public interface ReviewLikeService {
    Long saveReviewLike(Long reviewId);

    ReviewLike findReviewLike(Long userId, Long reviewId);

    void deleteReviewLike(Long reviewId);
}

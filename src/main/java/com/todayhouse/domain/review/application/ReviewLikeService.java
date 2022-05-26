package com.todayhouse.domain.review.application;

import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.domain.ReviewLike;

import java.util.List;

public interface ReviewLikeService {
    Long saveReviewLike(Long reviewId);

    void deleteReviewLike(Long reviewId);

    List<ReviewLike> findMyReviewLikesInReviews(List<Review> reviews);
}

package com.todayhouse.domain.review.application;

import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.dto.request.ReviewSearchRequest;
import com.todayhouse.domain.review.dto.response.ReviewRatingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ReviewService {
    Long saveReview(MultipartFile multipartFile, Review review, Long productId);

    Page<Review> findReviews(ReviewSearchRequest request, Pageable pageable);

    ReviewRatingResponse findReviewRatingByProductId(Long productId);

    boolean canWriteReview(Long productId);

    void deleteReview(Long productId);
}

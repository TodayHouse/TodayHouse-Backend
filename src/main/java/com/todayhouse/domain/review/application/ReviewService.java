package com.todayhouse.domain.review.application;

import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.dto.request.ReviewSaveRequest;
import com.todayhouse.domain.review.dto.request.ReviewSearchRequest;
import com.todayhouse.domain.review.dto.response.ReviewRatingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ReviewService {
    Page<Review> findReviews(ReviewSearchRequest request, Pageable pageable);

    Long saveReview(MultipartFile multipartFile, ReviewSaveRequest request);

    ReviewRatingResponse findReviewRatingByProductId(Long productId);

    boolean canWriteReview(Long productId);

    void deleteReview(Long productId);
}

package com.todayhouse.domain.review.dao;

import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.dto.request.ReviewSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomReviewRepository {
    Page<Review> findAllReviews(ReviewSearchRequest request, Pageable pageable);
}

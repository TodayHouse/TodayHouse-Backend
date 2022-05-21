package com.todayhouse.domain.review.dao;

import com.todayhouse.domain.review.domain.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    long countByReviewId(Long reviewId);

    Optional<ReviewLike> findByUserIdAndReviewId(Long userId, Long reviewId);
}

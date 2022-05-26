package com.todayhouse.domain.review.dao;

import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.domain.ReviewLike;
import com.todayhouse.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    long countByReview(Review review);

    Optional<ReviewLike> findByUserAndReview(User user, Review review);

    List<ReviewLike> findByUserAndReviewIn(User user, List<Review> reviews);
}

package com.todayhouse.domain.review.dao;

import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.dto.ReviewRating;
import com.todayhouse.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, CustomReviewRepository {
    @Query("select new com.todayhouse.domain.review.dto.ReviewRating(r.rating, count(r)) " +
            "from Review r " +
            "where r.product.id=:productId " +
            "group by r.rating " +
            "order by r.rating")
    List<ReviewRating> countReviewByProductIdGroupByRating(@Param("productId") Long productId);

    Optional<Review> findByUserAndProductId(User user, Long productId);

    Optional<Review> findByIdAndUser(Long reviewId, User user);
}

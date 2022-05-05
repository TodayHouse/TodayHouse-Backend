package com.todayhouse.domain.review.dao;

import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.dto.response.ReviewRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, CustomReviewRepository {
    @Query("select new com.todayhouse.domain.review.dto.response.ReviewRating(r.rating, count(r)) " +
            "from Review r " +
            "where r.product.id=:productId " +
            "group by r.rating " +
            "order by r.rating desc")
    List<ReviewRating> countReviewByProductIdGroupByRatingDesc(@Param("productId") Long productId);
}

package com.todayhouse.domain.review.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.domain.ReviewLike;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class ReviewLikeRepositoryTest extends DataJpaBase {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ReviewLikeRepository reviewLikeRepository;

    User user1, user2;
    Review review1;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(User.builder().build());
        user2 = userRepository.save(User.builder().build());
        review1 = reviewRepository.save(Review.builder().rating(5).user(user1).build());
    }

    @Test
    @DisplayName("Review id로 리뷰 개수 조회")
    void countByReviewId() {
        reviewLikeRepository.save(new ReviewLike(user1, review1));
        reviewLikeRepository.save(new ReviewLike(user2, review1));

        long count = reviewLikeRepository.countByReview(review1);

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("user id와 review id로 reviewLike 조회")
    void findByUserIdAndReviewId() {
        ReviewLike reviewLike = reviewLikeRepository.save(new ReviewLike(user1, review1));

        ReviewLike find = reviewLikeRepository.findByUserAndReview(user1, review1).orElse(null);

        assertThat(find.getId()).isEqualTo(reviewLike.getId());
    }

    @Test
    @DisplayName("Review가 포함된 ReviewLike 조회")
    void findByReviewIn() {
        ReviewLike reviewLike1 = reviewLikeRepository.save(new ReviewLike(user1, review1));
        reviewLikeRepository.save(new ReviewLike(user2, review1));
        List<Review> reviews = List.of(review1);

        List<ReviewLike> find = reviewLikeRepository.findByUserAndReviewIn(user1, reviews);
        assertThat(find).containsOnlyOnce(reviewLike1);
    }
}
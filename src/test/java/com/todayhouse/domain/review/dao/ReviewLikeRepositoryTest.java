package com.todayhouse.domain.review.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.review.domain.Rating;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.domain.ReviewLike;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ReviewLikeRepositoryTest extends DataJpaBase {
    @Autowired
    TestEntityManager em;

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
        review1 = reviewRepository.save(Review.builder().rating(new Rating(5,5,5,5,5)).user(user1).build());
    }

    @Test
    @DisplayName("Review id로 리뷰 개수 조회")
    void countByReviewId() {
        reviewLikeRepository.save(new ReviewLike(user1, review1));
        reviewLikeRepository.save(new ReviewLike(user2, review1));
        em.flush();
        em.clear();

        long count = reviewLikeRepository.countByReviewId(review1.getId());

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("user id와 review id로 reviewLike 조회")
    void findByUserIdAndReviewId() {
        ReviewLike reviewLike = reviewLikeRepository.save(new ReviewLike(user1, review1));
        em.flush();
        em.clear();

        ReviewLike find = reviewLikeRepository.findByUserIdAndReviewId(user1.getId(), review1.getId()).orElse(null);

        assertThat(find.getId()).isEqualTo(reviewLike.getId());
    }
}
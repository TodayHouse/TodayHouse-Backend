package com.todayhouse.domain.review.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.dto.request.ReviewSearchRequest;
import com.todayhouse.domain.review.dto.response.ReviewRating;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ReviewRepositoryTest extends DataJpaBase {
    @Autowired
    TestEntityManager em;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ProductRepository productRepository;

    User u1, u2;
    Product p1;
    Review r1, r2, r3, r4, r5;

    @BeforeEach
    void setUp() {
        u1 = User.builder().nickname("u1").build();
        u2 = User.builder().nickname("u2").build();
        p1 = Product.builder().title("p1").build();

        r1 = Review.builder().reviewImage("r1Img").rating(1).user(u1).product(p1).build();
        r2 = Review.builder().reviewImage("r2Img").rating(2).user(u1).product(p1).build();
        r3 = Review.builder().reviewImage("r3Img").rating(5).user(u2).product(p1).build();
        r4 = Review.builder().reviewImage("r4Img").rating(4).user(u1).product(p1).build();
        r5 = Review.builder().user(u1).product(p1).rating(4).build();

        em.persist(u1);
        em.persist(u2);
        em.persist(p1);

        em.persist(r1);
        em.persist(r2);
        em.persist(r3);
        em.persist(r4);
        em.persist(r5);
    }

    @Test
    @DisplayName("Review paging으로 u1의 리뷰 최신순 조회")
    void findAllReviewsLatest() {
        ReviewSearchRequest reviewSearchRequest = new ReviewSearchRequest(null, u1.getId(), null, false);
        PageRequest page = PageRequest.of(0, 2, Sort.by("createdAt").descending());

        Page<Review> reviews = reviewRepository.findAllReviews(reviewSearchRequest, page);

        assertThat(reviews.getTotalPages()).isEqualTo(2);
        assertThat(reviews.getTotalElements()).isEqualTo(4);
        assertThat(reviews.getContent().get(0).getId()).isEqualTo(r5.getId());
        assertThat(reviews.getContent().get(1).getId()).isEqualTo(r4.getId());
    }

    @Test
    @DisplayName("Review paging으로 사진 리뷰 추천순 조회")
    void findAllReviewsRatingWithImg() {
        ReviewSearchRequest reviewSearchRequest = new ReviewSearchRequest(null, null, null, true);
        PageRequest page = PageRequest.of(0, 2, Sort.by("rating").descending());

        Page<Review> reviews = reviewRepository.findAllReviews(reviewSearchRequest, page);

        assertThat(reviews.getTotalPages()).isEqualTo(2);
        assertThat(reviews.getTotalElements()).isEqualTo(4);
        assertThat(reviews.getContent().get(0).getId()).isEqualTo(r3.getId());
        assertThat(reviews.getContent().get(1).getId()).isEqualTo(r4.getId());
    }

    @Test
    @DisplayName("Review paging으로 4점 review 최신순 조회")
    void findAllReviewsRating5() {
        ReviewSearchRequest reviewSearchRequest = new ReviewSearchRequest(4, null, null, null);
        PageRequest page = PageRequest.of(0, 2, Sort.by("createdAt").descending());

        Page<Review> reviews = reviewRepository.findAllReviews(reviewSearchRequest, page);

        assertThat(reviews.getTotalPages()).isEqualTo(1);
        assertThat(reviews.getTotalElements()).isEqualTo(2);
        assertThat(reviews.getContent().get(0).getId()).isEqualTo(r5.getId());
        assertThat(reviews.getContent().get(1).getId()).isEqualTo(r4.getId());
    }

    @Test
    @DisplayName("Review paging으로 product p1 최신순 조회")
    void findAllReviewsProductP1() {
        ReviewSearchRequest reviewSearchRequest = new ReviewSearchRequest(null, null, p1.getId(), null);
        PageRequest page = PageRequest.of(0, 2, Sort.by("createdAt").descending());

        Page<Review> reviews = reviewRepository.findAllReviews(reviewSearchRequest, page);

        assertThat(reviews.getTotalPages()).isEqualTo(3);
        assertThat(reviews.getTotalElements()).isEqualTo(5);
        assertThat(reviews.getContent().get(0).getId()).isEqualTo(r5.getId());
        assertThat(reviews.getContent().get(1).getId()).isEqualTo(r4.getId());
    }

    @Test
    @DisplayName("Reivew 평점 별 개수 조회")
    void countReviewGroupByRating() {
        List<ReviewRating> reviewRatings = reviewRepository.countReviewByProductIdGroupByRatingDesc(p1.getId());

        List<Integer> rating = List.of(5, 4, 2, 1);
        List<Long> count = List.of(1L, 2L, 1L, 1L);
        for (int i = 0; i < reviewRatings.size(); i++) {
            assertThat(reviewRatings.get(i).getRating()).isEqualTo(rating.get(i));
            assertThat(reviewRatings.get(i).getCount()).isEqualTo(count.get(i));
        }
    }
}
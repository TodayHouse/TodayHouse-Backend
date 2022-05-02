package com.todayhouse.domain.review.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.dto.request.ReviewSearchRequest;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ReviewRepositoryTest extends DataJpaBase {
    @Autowired
    TestEntityManager em;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ProductRepository productRepository;

    @Test
    @DisplayName("Review paging으로 u1의 리뷰 최신순 조회")
    void findAllReviewsLatest() {
        User u1 = User.builder().nickname("u1").build();
        User u2 = User.builder().nickname("u2").build();
        Product p1 = Product.builder().title("p1").build();

        Review r1 = Review.builder().reviewImage("r1Img").user(u1).product(p1).build();
        Review r2 = Review.builder().reviewImage("r2Img").user(u1).product(p1).build();
        Review r3 = Review.builder().reviewImage("r3Img").user(u2).product(p1).build();
        Review r4 = Review.builder().reviewImage("r4Img").user(u1).product(p1).build();

        em.persist(u1);
        em.persist(u2);
        em.persist(p1);

        em.persist(r1);
        em.persist(r2);
        em.persist(r3);
        em.persist(r4);

        ReviewSearchRequest reviewSearchRequest = new ReviewSearchRequest(null, u1.getId(), false);
        PageRequest page = PageRequest.of(0, 2, Sort.by("createdAt").descending());
        Page<Review> reviews = reviewRepository.findAllReviews(reviewSearchRequest, page);

        assertThat(reviews.getTotalPages()).isEqualTo(2);
        assertThat(reviews.getTotalElements()).isEqualTo(3);
        assertThat(reviews.getContent().get(0).getId()).isEqualTo(r4.getId());
        assertThat(reviews.getContent().get(1).getId()).isEqualTo(r2.getId());
    }

    @Test
    @DisplayName("Review paging으로 사진 리뷰 추천순 조회")
    void findAllReviewsRatingWithImg(){
        User u1 = User.builder().nickname("u1").build();
        Product p1 = Product.builder().title("p1").build();

        Review r1 = Review.builder().reviewImage("r1Img").rating(1).user(u1).product(p1).build();
        Review r2 = Review.builder().reviewImage("r2Img").rating(5).user(u1).product(p1).build();
        Review r3 = Review.builder().user(u1).rating(3).product(p1).build();
        Review r4 = Review.builder().reviewImage("r4Img").rating(2).user(u1).product(p1).build();

        em.persist(u1);
        em.persist(p1);

        em.persist(r1);
        em.persist(r2);
        em.persist(r3);
        em.persist(r4);

        ReviewSearchRequest reviewSearchRequest = new ReviewSearchRequest(null, u1.getId(), true);
        PageRequest page = PageRequest.of(0, 2, Sort.by("rating").descending());
        Page<Review> reviews = reviewRepository.findAllReviews(reviewSearchRequest, page);

        assertThat(reviews.getTotalPages()).isEqualTo(2);
        assertThat(reviews.getTotalElements()).isEqualTo(3);
        assertThat(reviews.getContent().get(0).getId()).isEqualTo(r2.getId());
        assertThat(reviews.getContent().get(1).getId()).isEqualTo(r4.getId());
    }

    @Test
    @DisplayName("Review paging으로 5점 review 최신순 조회")
    void findAllReviewsRating5(){
        User u1 = User.builder().nickname("u1").build();
        User u2 = User.builder().nickname("u2").build();
        Product p1 = Product.builder().title("p1").build();

        Review r1 = Review.builder().rating(4).user(u1).product(p1).build();
        Review r2 = Review.builder().rating(5).user(u1).product(p1).build();
        Review r3 = Review.builder().rating(5).user(u2).product(p1).build();
        Review r4 = Review.builder().rating(1).user(u1).product(p1).build();

        em.persist(u1);
        em.persist(u2);
        em.persist(p1);

        em.persist(r1);
        em.persist(r2);
        em.persist(r3);
        em.persist(r4);

        ReviewSearchRequest reviewSearchRequest = new ReviewSearchRequest(5, null, null);
        PageRequest page = PageRequest.of(0, 2, Sort.by("createdAt").descending());
        Page<Review> reviews = reviewRepository.findAllReviews(reviewSearchRequest, page);

        assertThat(reviews.getTotalPages()).isEqualTo(1);
        assertThat(reviews.getTotalElements()).isEqualTo(2);
        assertThat(reviews.getContent().get(0).getId()).isEqualTo(r3.getId());
        assertThat(reviews.getContent().get(1).getId()).isEqualTo(r2.getId());
    }
}
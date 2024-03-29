package com.todayhouse.domain.review.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.dto.ReviewRating;
import com.todayhouse.domain.review.dto.request.ReviewSearchRequest;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReviewRepositoryTest extends DataJpaBase {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ProductRepository productRepository;

    User u1, u2;
    Product p1, p2, p3, p4;
    Review r1, r2, r3, r4, r5;

    @BeforeEach
    void setUp() {
        u1 = userRepository.save(User.builder().nickname("u1").build());
        u2 = userRepository.save(User.builder().nickname("u2").build());

        p1 = productRepository.save(Product.builder().title("p1").build());
        p2 = productRepository.save(Product.builder().title("p1").build());
        p3 = productRepository.save(Product.builder().title("p1").build());
        p4 = productRepository.save(Product.builder().title("p1").build());

        r1 = Review.builder()
                .reviewImage("r1Img").rating(1)
                .user(u1).product(p1).build();
        r2 = Review.builder()
                .reviewImage("r2Img").rating(2)
                .user(u1).product(p2).build();
        r3 = Review.builder()
                .reviewImage("r3Img").rating(3)
                .user(u2).product(p1).build();
        r4 = Review.builder()
                .reviewImage("r4Img").rating(4)
                .user(u1).product(p3).build();
        r5 = Review.builder()
                .rating(4)
                .user(u1).product(p4).build();

        reviewRepository.save(r1);
        reviewRepository.save(r2);
        reviewRepository.save(r3);
        reviewRepository.save(r4);
        reviewRepository.save(r5);
    }

    @Test
    @DisplayName("Review paging으로 u1의 리뷰 최신순 조회")
    void findAllReviewsLatest() {
        ReviewSearchRequest reviewSearchRequest = new ReviewSearchRequest(u1.getId(), null, null, false);
        PageRequest page = PageRequest.of(0, 2, Sort.by("id").descending());

        Page<Review> reviews = reviewRepository.findAllReviews(reviewSearchRequest, page);

        assertThat(reviews.getTotalPages()).isEqualTo(2);
        assertThat(reviews.getTotalElements()).isEqualTo(4);
        assertThat(reviews.getContent().get(0).getId()).isEqualTo(r5.getId());
        assertThat(reviews.getContent().get(1).getId()).isEqualTo(r4.getId());
    }

    @Test
    @DisplayName("Review paging으로 사진 리뷰 평점순 조회")
    void findAllReviewsRatingWithImg() {
        ReviewSearchRequest reviewSearchRequest = new ReviewSearchRequest(null, null, null, true);
        PageRequest page = PageRequest.of(0, 2, Sort.by("rating").descending());

        Page<Review> reviews = reviewRepository.findAllReviews(reviewSearchRequest, page);

        assertThat(reviews.getTotalPages()).isEqualTo(2);
        assertThat(reviews.getTotalElements()).isEqualTo(4);
        assertThat(reviews.getContent().get(0).getId()).isEqualTo(r4.getId());
        assertThat(reviews.getContent().get(1).getId()).isEqualTo(r3.getId());
    }

    @Test
    @DisplayName("Review paging으로 4점 review id순 조회")
    void findAllReviewsRating4() {
        ReviewSearchRequest reviewSearchRequest =
                new ReviewSearchRequest(null, null, "4", null);
        PageRequest page = PageRequest.of(0, 2, Sort.by("id").descending());

        Page<Review> reviews = reviewRepository.findAllReviews(reviewSearchRequest, page);

        assertThat(reviews.getTotalPages()).isEqualTo(1);
        assertThat(reviews.getTotalElements()).isEqualTo(2);
        assertThat(reviews.getContent().get(0).getId()).isEqualTo(r5.getId());
        assertThat(reviews.getContent().get(1).getId()).isEqualTo(r4.getId());
    }

    @Test
    @DisplayName("Review paging으로 product p1 id순 조회")
    void findAllReviewsProductP1() {
        ReviewSearchRequest reviewSearchRequest = new ReviewSearchRequest(null, p1.getId(), null, null);
        PageRequest page = PageRequest.of(0, 2, Sort.by("id").descending());

        Page<Review> reviews = reviewRepository.findAllReviews(reviewSearchRequest, page);

        assertThat(reviews.getTotalPages()).isEqualTo(1);
        assertThat(reviews.getTotalElements()).isEqualTo(2);
        assertThat(reviews.getContent().get(0).getId()).isEqualTo(r3.getId());
        assertThat(reviews.getContent().get(1).getId()).isEqualTo(r1.getId());
    }

    @Test
    @DisplayName("p1 Reivew 평점 별 개수 조회")
    void countReviewGroupByRating() {
        User u3 = User.builder().nickname("u3").build();
        User u4 = User.builder().nickname("u4").build();
        User u5 = User.builder().nickname("u5").build();

        Review r6 = Review.builder()
                .user(u3).product(p1).rating(2).build();
        Review r7 = Review.builder()
                .user(u4).product(p1).rating(4).build();
        Review r8 = Review.builder()
                .user(u5).product(p1).rating(4).build();

        userRepository.save(u3);
        userRepository.save(u4);
        userRepository.save(u5);
        reviewRepository.save(r6);
        reviewRepository.save(r7);
        reviewRepository.save(r8);

        List<Integer> rating = List.of(1, 2, 3, 4);
        List<Long> count = List.of(1L, 1L, 1L, 2L);

        List<ReviewRating> reviewRatings = reviewRepository.countReviewByProductIdGroupByRating(p1.getId());

        for (int i = 0; i < reviewRatings.size(); i++) {
            assertThat(reviewRatings.get(i).getRating()).isEqualTo(rating.get(i));
            assertThat(reviewRatings.get(i).getCount()).isEqualTo(count.get(i));
        }
    }

    @Test
    @DisplayName("Review를 userId, productId 로 찾기")
    void findByUserId() {
        Review review = reviewRepository.findByUserAndProductId(u1, p1.getId()).orElse(null);

        assertThat(review).usingRecursiveComparison().isEqualTo(r1);
    }

    @Test
    @DisplayName("Review Rating split 예외")
    void findAllReviewsSplitException() {
        ReviewSearchRequest reviewSearchRequest = new ReviewSearchRequest(null, p1.getId(), "1a1", null);
        PageRequest page = PageRequest.of(0, 2, Sort.by("id").descending());

        assertThrows(RuntimeException.class, () -> reviewRepository.findAllReviews(reviewSearchRequest, page));
    }

    @Test
    @DisplayName("페이징으로 조회한 review가 0")
    void findAllReviews() {
        ReviewSearchRequest reviewSearchRequest = new ReviewSearchRequest(null, p1.getId() + 100L, null, null);
        PageRequest page = PageRequest.of(0, 2, Sort.by("id").descending());

        Page<Review> reviews = reviewRepository.findAllReviews(reviewSearchRequest, page);

        assertThat(reviews.getTotalPages()).isEqualTo(0);
        assertThat(reviews.getTotalElements()).isEqualTo(0);
    }
}
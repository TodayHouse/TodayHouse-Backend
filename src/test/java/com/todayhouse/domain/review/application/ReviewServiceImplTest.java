package com.todayhouse.domain.review.application;

import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.exception.ProductNotFoundException;
import com.todayhouse.domain.review.dao.ReviewRepository;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.dto.ReviewRating;
import com.todayhouse.domain.review.dto.request.ReviewSaveRequest;
import com.todayhouse.domain.review.dto.response.ReviewRatingResponse;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import com.todayhouse.infra.S3Storage.service.FileService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Autowired
    MockMvc mock;

    @InjectMocks
    ReviewServiceImpl reviewService;

    @Mock
    UserRepository userRepository;

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    FileService fileService;

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    MultipartFile file = mock(MultipartFile.class);
    Product product = mock(Product.class);
    User user = mock(User.class);
    String url = "image.com", email = "test@test";
    Long productId = 1L, reviewId = 10L;


    @Test
    @DisplayName("리뷰 저장")
    void saveReview() {
        ReviewSaveRequest request = new ReviewSaveRequest(5, productId, "Good");
        Review review = Review.builder().reviewImage(url).content("Good").build();
        ReflectionTestUtils.setField(review, "id", reviewId);

        setSecurityName(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(fileService.uploadImage(file)).thenReturn("byteImage");
        when(fileService.changeFileNameToUrl(anyString())).thenReturn(url);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        Long saveId = reviewService.saveReview(file, request);

        assertThat(saveId).isEqualTo(reviewId);
    }

    @Test
    @DisplayName("리뷰 저장 유효하지 않은 email")
    void reviewSaveEmailException() {
        ReviewSaveRequest request = new ReviewSaveRequest(5, productId, "Good");
        Review review = Review.builder().reviewImage(url).content("Good").build();
        ReflectionTestUtils.setField(review, "id", reviewId);

        setSecurityName(email);
        when(userRepository.findByEmail(email)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> reviewService.saveReview(file, request));
    }

    @Test
    @DisplayName("리뷰 저장 유효하지 않은 productId")
    void reviewSaveProductException() {
        ReviewSaveRequest request = new ReviewSaveRequest(5, productId, "Good");
        Review review = Review.builder().reviewImage(url).content("Good").build();
        ReflectionTestUtils.setField(review, "id", reviewId);

        setSecurityName(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(fileService.uploadImage(file)).thenReturn("byteImage");
        when(fileService.changeFileNameToUrl(anyString())).thenReturn(url);
        when(productRepository.findById(productId)).thenThrow(ProductNotFoundException.class);

        assertThrows(ProductNotFoundException.class, () -> reviewService.saveReview(file, request));
    }

    private void setSecurityName(String email) {
        Authentication authentication = mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("review 평점 별 개수 조회")
    void ReviewRatingResponse() {
        List<ReviewRating> reviewRatings =
                List.of(new ReviewRating(1, 2), new ReviewRating(2, 5), new ReviewRating(5, 3));
        ReviewRatingResponse result = ReviewRatingResponse.builder().one(2).two(5).five(3).build();

        when(reviewRepository.countReviewByProductIdGroupByRating(productId))
                .thenReturn(reviewRatings);

        ReviewRatingResponse response = reviewService.findReviewRatingByProductId(productId);

        assertThat(response).usingRecursiveComparison().isEqualTo(result);
        assertThat(response.getAverage()).isEqualTo(2.7);
    }
}
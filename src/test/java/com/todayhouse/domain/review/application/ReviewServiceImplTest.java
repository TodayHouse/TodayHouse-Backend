package com.todayhouse.domain.review.application;

import com.todayhouse.domain.order.dao.OrderRepository;
import com.todayhouse.domain.order.domain.Orders;
import com.todayhouse.domain.order.domain.Status;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.exception.ProductNotFoundException;
import com.todayhouse.domain.review.dao.ReviewRepository;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.dto.ReviewRating;
import com.todayhouse.domain.review.dto.request.ReviewSaveRequest;
import com.todayhouse.domain.review.dto.request.ReviewSearchRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Autowired
    MockMvc mock;

    @InjectMocks
    ReviewServiceImpl reviewService;

    @Mock
    UserRepository userRepository;

    @Mock
    OrderRepository orderRepository;

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
    Long productId = 1L, reviewId = 10L, userId = 1L;

    @Test
    @DisplayName("해당 상품의 리뷰 페이징 조회")
    void findReviews() {
        ReviewSearchRequest reviewSearchRequest = ReviewSearchRequest.builder().build();
        PageRequest pageRequest = PageRequest.of(1, 1);
        Page<Review> result = mock(Page.class);

        when(reviewRepository.findAllReviews(reviewSearchRequest, pageRequest)).thenReturn(result);

        Page<Review> reviews = reviewService.findReviews(reviewSearchRequest, pageRequest);

        assertThat(reviews).isEqualTo(result);
    }

    @Test
    @DisplayName("리뷰 저장")
    void saveReview() {
        ReviewSaveRequest request = new ReviewSaveRequest(5, productId, "Good");
        Review review = Review.builder().reviewImage(url).content("Good").build();
        ReflectionTestUtils.setField(review, "id", reviewId);

        setSecurityName(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(reviewRepository.findByUserIdAndProductId(userId, productId)).thenReturn(Optional.ofNullable(null));
        isCompletedOrderTrue();
        when(fileService.uploadImage(file)).thenReturn("byteImage");
        when(fileService.changeFileNameToUrl(anyString())).thenReturn(url);

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
        when(productRepository.findById(productId)).thenThrow(ProductNotFoundException.class);

        assertThrows(ProductNotFoundException.class, () -> reviewService.saveReview(file, request));
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

    @Test
    @DisplayName("리뷰 작성 가능한 유저")
    void canWriteReview() {
        setSecurityName(email);
        isCompletedOrderTrue();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(reviewRepository.findByUserIdAndProductId(userId, productId)).thenReturn(Optional.ofNullable(null));

        assertTrue(reviewService.canWriteReview(productId));
    }

    @Test
    @DisplayName("주문 완료된 상품이 없어 리뷰 작성 불가능")
    void cannotWriteReviewNoOrder() {
        setSecurityName(email);
        isCompletedOrderFalse();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertFalse(reviewService.canWriteReview(productId));
    }

    @Test
    @DisplayName("이미 리뷰를 작성하여 추가 작성 불가능")
    void cannotWriteReviewMore() {
        setSecurityName(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        isCompletedOrderTrue();
        when(reviewRepository.findByUserIdAndProductId(userId, productId)).thenReturn(Optional.of(mock(Review.class)));

        assertFalse(reviewService.canWriteReview(productId));
    }

    private void isCompletedOrderTrue() {
        getUserIdAndProductId();
        when(orderRepository.findByUserIdAndProductIdAndStatus(userId, productId, Status.COMPLETED))
                .thenReturn(List.of(mock(Orders.class)));
    }

    private void isCompletedOrderFalse() {
        getUserIdAndProductId();
        when(orderRepository.findByUserIdAndProductIdAndStatus(userId, productId, Status.COMPLETED))
                .thenReturn(List.of());
    }


    @Test
    @DisplayName("리뷰 삭제")
    void deleteReview(){
        setSecurityName(email);
        Review review = Review.builder().build();
        ReflectionTestUtils.setField(review,"id",reviewId);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(userId);
        when(reviewRepository.findByUserIdAndProductId(userId, productId)).thenReturn(Optional.of(review));

        reviewService.deleteReview(productId);

        verify(reviewRepository).deleteById(reviewId);
    }

    private void setSecurityName(String email) {
        Authentication authentication = mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }

    private void getUserIdAndProductId() {
        when(user.getId()).thenReturn(userId);
        when(product.getId()).thenReturn(productId);
    }
}
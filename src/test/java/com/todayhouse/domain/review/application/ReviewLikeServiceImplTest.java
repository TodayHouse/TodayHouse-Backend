package com.todayhouse.domain.review.application;

import com.todayhouse.domain.review.dao.ReviewLikeRepository;
import com.todayhouse.domain.review.dao.ReviewRepository;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.domain.ReviewLike;
import com.todayhouse.domain.review.exception.InvalidReviewLikeException;
import com.todayhouse.domain.review.exception.ReviewLikeDuplicationException;
import com.todayhouse.domain.review.exception.ReviewLikeNotFoundException;
import com.todayhouse.domain.review.exception.ReviewNotFoundException;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserNotFoundException;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewLikeServiceImplTest {

    @Autowired
    MockMvc mock;
    @InjectMocks
    ReviewLikeServiceImpl reviewLikeService;
    @Mock
    UserRepository userRepository;
    @Mock
    ReviewRepository reviewRepository;
    @Mock
    ReviewLikeRepository reviewLikeRepository;

    String email = "test@email.com";
    User user = User.builder().id(1L).email(email).build();

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("도움이 돼요 저장")
    void saveReviewLike() {
        setSecurityName(email);
        User writer = User.builder().id(2L).build();
        Review review = Review.builder().user(writer).build();
        ReflectionTestUtils.setField(review, "id", 1L);
        ReviewLike reviewLike = mock(ReviewLike.class);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
        when(reviewLikeRepository.findByUserAndReview(any(User.class), any(Review.class))).thenReturn(Optional.ofNullable(null));
        when(reviewLikeRepository.save(any(ReviewLike.class))).thenReturn(reviewLike);
        when(reviewLike.getId()).thenReturn(10L);

        Long id = reviewLikeService.saveReviewLike(1L);
        assertThat(id).isEqualTo(10L);
    }

    @Test
    @DisplayName("존재하지 않는 리뷰에 좋아요 예외처리")
    void saveReviewLikeReviewNotExist() {
        setSecurityName(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        assertThrows(ReviewNotFoundException.class, () -> reviewLikeService.saveReviewLike(1L));
    }

    @Test
    @DisplayName("자신의 리뷰에 좋아요 예외처리")
    void saveReviewLikeSelfException() {
        setSecurityName(email);
        Review review = Review.builder().user(user).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));

        assertThrows(InvalidReviewLikeException.class, () -> reviewLikeService.saveReviewLike(1L));
    }

    @Test
    @DisplayName("좋아요는 한 번만 가능")
    void saveReviewLikeDuplication() {
        setSecurityName(email);
        User writer = User.builder().id(2L).build();
        Review review = Review.builder().user(writer).build();
        ReflectionTestUtils.setField(review, "id", 1L);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
        when(reviewLikeRepository.findByUserAndReview(any(User.class), any(Review.class))).thenReturn(Optional.of(mock(ReviewLike.class)));

        assertThrows(ReviewLikeDuplicationException.class, () -> reviewLikeService.saveReviewLike(1L));
    }

    @Test
    @DisplayName("좋아요 삭제")
    void deleteReviewLike() {
        setSecurityName(email);
        Review review = mock(Review.class);
        User user = mock(User.class);
        ReviewLike reviewLike = mock(ReviewLike.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
        when(reviewLikeRepository.findByUserAndReview(user, review))
                .thenReturn(Optional.of(reviewLike));
        doNothing().when(reviewLikeRepository).delete(reviewLike);

        reviewLikeService.deleteReviewLike(1L);

        verify(reviewLikeRepository).delete(reviewLike);
    }

    @Test
    @DisplayName("존재하지 않는 user id로 삭제는 예외처리")
    void deleteReviewLikeUserIdException() {
        setSecurityName(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(null));

        assertThrows(UserNotFoundException.class, () -> reviewLikeService.deleteReviewLike(1L));
    }

    @Test
    @DisplayName("존재하지 않는 review id로 삭제는 예외처리")
    void deleteReviewLikeReviewIdException() {
        setSecurityName(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        assertThrows(ReviewNotFoundException.class, () -> reviewLikeService.deleteReviewLike(1L));
    }

    @Test
    @DisplayName("존재하지 않는 reviewLike 삭제는 예외처리")
    void deleteReviewLikeReviewLikeException() {
        Review review = mock(Review.class);

        setSecurityName(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.ofNullable(review));
        when(reviewLikeRepository.findByUserAndReview(user, review))
                .thenReturn(Optional.ofNullable(null));
        assertThrows(ReviewLikeNotFoundException.class, () -> reviewLikeService.deleteReviewLike(1L));
    }

    @Test
    @DisplayName("Reivew가 포함된 자신의 ReviewLike 조회")
    void findMyReviewLikesWithReviews() {
        List<Review> reviews = mock(List.class);
        List<ReviewLike> reviewLikes = List.of(mock(ReviewLike.class));
        setSecurityName(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(reviewLikeRepository.findByUserAndReviewIn(any(User.class), anyList())).thenReturn(reviewLikes);

        assertThat(reviewLikeService.findMyReviewLikesInReviews(reviews)).isEqualTo(reviewLikes);
    }

    private void setSecurityName(String email) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }
}
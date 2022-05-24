package com.todayhouse.domain.review.application;

import com.todayhouse.domain.review.dao.ReviewLikeRepository;
import com.todayhouse.domain.review.dao.ReviewRepository;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.domain.ReviewLike;
import com.todayhouse.domain.review.exception.InvalidReviewLikeException;
import com.todayhouse.domain.review.exception.ReviewLikeDuplicationException;
import com.todayhouse.domain.review.exception.ReviewNotFoundException;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewLikeServiceImpl implements ReviewLikeService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    @Override
    public Long saveReviewLike(Long reviewId) {
        User user = findUser();
        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
        ReviewLike reviewLike = syncSaveReviewLike(user, review);
        return reviewLike.getId();
    }

    private synchronized ReviewLike syncSaveReviewLike(User user, Review review) {
        checkReviewValidation(user, review);
        ReviewLike reviewLike = new ReviewLike(user, review);
        return reviewLikeRepository.save(reviewLike);
    }

    private void checkReviewValidation(User user, Review review) {
        if (review.getUser().getId() == user.getId()) {
            throw new InvalidReviewLikeException();
        }
        Optional<ReviewLike> reviewLike = reviewLikeRepository.findByUserAndReview(user, review);
        reviewLike.ifPresent(r -> {
            throw new ReviewLikeDuplicationException();
        });
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewLike findReviewLike(Long userId, Long reviewId) {

        return null;
    }

    @Override
    public void deleteReviewLike(Long reviewId) {
        User user = findUser();
        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
        reviewLikeRepository.delete(reviewLikeRepository.findByUserAndReview(user, review)
                .orElseThrow(InvalidReviewLikeException::new));
    }

    private User findUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return user;
    }
}

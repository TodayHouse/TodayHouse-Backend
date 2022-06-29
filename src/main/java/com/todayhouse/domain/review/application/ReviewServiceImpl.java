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
import com.todayhouse.domain.review.dto.request.ReviewSearchRequest;
import com.todayhouse.domain.review.dto.response.ReviewRatingResponse;
import com.todayhouse.domain.review.exception.OrderNotCompletedException;
import com.todayhouse.domain.review.exception.ReviewDuplicateException;
import com.todayhouse.domain.review.exception.ReviewNotFoundException;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import com.todayhouse.infra.S3Storage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final FileService fileService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<Review> findReviews(ReviewSearchRequest request, Pageable pageable) {
        return reviewRepository.findAllReviews(request, pageable);
    }

    @Override
    public synchronized Long saveReview(MultipartFile multipartFile, Review review, Long productId) {
        User user = getValidUser();
        Product product = getValidProduct(productId);

        if (!isOrderCompleteUser(user.getId(), product.getId()))
            throw new OrderNotCompletedException();

        return updateAndSaveReview(review, multipartFile, user, product).getId();
    }

    private User getValidUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    private String saveFileAndGetUrl(MultipartFile multipartFile) {
        if (multipartFile == null) return null;
        String fileName = fileService.uploadImage(multipartFile);
        return fileService.changeFileNameToUrl(fileName);
    }

    private Product getValidProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
    }

    @Transactional
    public Review updateAndSaveReview(Review review, MultipartFile multipartFile, User user, Product product) {
        String imageUrl = saveFileAndGetUrl(multipartFile);

        review.updateUser(user);
        review.updateProduct(product);
        review.updateReviewImageUrl(imageUrl);

        checkReviewDuplication(user, product.getId());
        return reviewRepository.save(review);
    }

    private void checkReviewDuplication(User user, Long productId) {
        reviewRepository.findByUserAndProductId(user, productId)
                .ifPresent(r -> {
                    throw new ReviewDuplicateException();
                });
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewRatingResponse findReviewRatingByProductId(Long productId) {
        List<ReviewRating> reviewRatings = reviewRepository.countReviewByProductIdGroupByRating(productId);
        List<Long> rating = new ArrayList<>(Collections.nCopies(6, 0L));
        reviewRatings.forEach(info -> rating.set(info.getRating(), info.getCount()));
        return ReviewRatingResponse.builder()
                .one(rating.get(1))
                .two(rating.get(2))
                .three(rating.get(3))
                .four(rating.get(4))
                .five(rating.get(5)).build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canWriteReview(Long productId) {
        User user = getValidUser();
        if (!isOrderCompleteUser(user.getId(), productId))
            return false;
        Optional<Review> review = reviewRepository.findByUserAndProductId(user, productId);
        return review.isEmpty();
    }

    private boolean isOrderCompleteUser(Long userId, Long productId) {
        List<Orders> orders = orderRepository.findByUserIdAndProductIdAndStatus(userId, productId, Status.COMPLETED);
        return orders.size() != 0;
    }

    @Override
    public void deleteReview(Long reviewId) {
        User user = getValidUser();
        Review review = reviewRepository.findByIdAndUser(reviewId, user).orElseThrow(ReviewNotFoundException::new);
        deleteImage(review);
        reviewRepository.deleteById(review.getId());
    }

    private void deleteImage(Review review) {
        String reviewUrl = review.getReviewImageUrl();
        if (ObjectUtils.isEmpty(reviewUrl))
            return;
        String fileName = reviewUrl.substring(reviewUrl.lastIndexOf('/') + 1);
        fileService.deleteOne(fileName);
    }
}

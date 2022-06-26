package com.todayhouse.domain.review.api;

import com.todayhouse.domain.review.application.ReviewLikeService;
import com.todayhouse.domain.review.application.ReviewService;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.domain.ReviewLike;
import com.todayhouse.domain.review.dto.request.ReviewSaveRequest;
import com.todayhouse.domain.review.dto.request.ReviewSearchRequest;
import com.todayhouse.domain.review.dto.response.ReviewRatingResponse;
import com.todayhouse.domain.review.dto.response.ReviewResponse;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.global.common.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewLikeService reviewLikeService;

    @PostMapping
    public BaseResponse<Long> saveReview(@RequestPart(value = "file", required = false) MultipartFile multipartFile,
                                         @RequestPart(value = "request") @Valid ReviewSaveRequest reviewSaveRequest) {
        Long saveId = reviewService.saveReview(multipartFile, reviewSaveRequest.toEntity(), reviewSaveRequest.getProductId());
        return new BaseResponse(saveId);
    }

    //?size=2&page=0&sort=createdAt,DESC&isImage=true
    //평점을 정렬할 땐 rating
    @GetMapping
    public BaseResponse<PageDto<ReviewResponse>> findReviews(@Valid @ModelAttribute ReviewSearchRequest reviewSearchRequest,
                                                             @PageableDefault Pageable pageable) {
        Page<Review> reviews = reviewService.findReviews(reviewSearchRequest, pageable);
        List<ReviewLike> reviewLikes = reviewLikeService.findMyReviewLikesInReviews(reviews.getContent());
        Map<Long, Boolean> liked = getLikedMap(reviewLikes);
        return new BaseResponse(new PageDto<>(reviews.map(review ->
                new ReviewResponse(review, !liked.getOrDefault(review.getId(), false)))));
    }

    private Map<Long, Boolean> getLikedMap(List<ReviewLike> reviewLikes) {
        Map<Long, Boolean> liked = new HashMap<>();
        reviewLikes.forEach(reviewLike -> liked.put(reviewLike.getReview().getId(), true));
        return liked;
    }

    @GetMapping("/ratings/{productId}")
    public BaseResponse<ReviewRatingResponse> findReviewRatings(@PathVariable("productId") Long productId) {
        ReviewRatingResponse reviewRatingResponse = reviewService.findReviewRatingByProductId(productId);
        return new BaseResponse<>(reviewRatingResponse);
    }

    @GetMapping("/writing-validity/{productId}")
    public BaseResponse<Boolean> canReviewWrite(@PathVariable("productId") Long productId) {
        boolean canWrite = reviewService.canWriteReview(productId);
        return new BaseResponse<>(canWrite);
    }

    @DeleteMapping("/{reviewId}")
    public BaseResponse deleteReview(@PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return new BaseResponse("리뷰가 삭제되었습니다.");
    }

    @PostMapping("/like/{reviewId}")
    public BaseResponse<Long> saveReviewLike(@PathVariable("reviewId") Long reviewId) {
        Long id = reviewLikeService.saveReviewLike(reviewId);
        return new BaseResponse(id);
    }

    @DeleteMapping("/like/{reviewId}")
    public BaseResponse<Long> deleteReviewLike(@PathVariable("reviewId") Long reviewId) {
        reviewLikeService.deleteReviewLike(reviewId);
        return new BaseResponse("삭제되었습니다.");
    }
}


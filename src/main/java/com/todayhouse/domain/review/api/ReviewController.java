package com.todayhouse.domain.review.api;

import com.todayhouse.domain.review.application.ReviewService;
import com.todayhouse.domain.review.domain.Review;
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

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public BaseResponse<Long> saveReview(@RequestPart(value = "file", required = false) MultipartFile multipartFile,
                                         @RequestPart(value = "request") @Valid ReviewSaveRequest reviewSaveRequest) {
        Long saveId = reviewService.saveReview(multipartFile, reviewSaveRequest);
        return new BaseResponse(saveId);
    }

    //?size=2&page=0&sort=createdAt,DESC&isImage=true
    @GetMapping
    public BaseResponse<PageDto<ReviewResponse>> findReviews(@ModelAttribute ReviewSearchRequest reviewSearchRequest,
                                                             @PageableDefault Pageable pageable) {
        Page<Review> reviews = reviewService.findReviews(reviewSearchRequest, pageable);
        PageDto<ReviewResponse> reviewResponses = new PageDto<>(reviews.map(review -> new ReviewResponse(review)));
        return new BaseResponse<>(reviewResponses);
    }

    @GetMapping("/ratings/{productId}")
    public BaseResponse<ReviewRatingResponse> findReviewRatings(@PathVariable("productId") Long productId) {
        ReviewRatingResponse reviewRatingResponse = reviewService.findReviewRatingByProductId(productId);
        return new BaseResponse<>(reviewRatingResponse);
    }

    @GetMapping("/writing-validity/{productId}")
    public BaseResponse<Boolean> canReviewWrite(@PathVariable("productId") Long productId){
        boolean canWrite = reviewService.canWriteReview(productId);
        return new BaseResponse<>(canWrite);
    }
}


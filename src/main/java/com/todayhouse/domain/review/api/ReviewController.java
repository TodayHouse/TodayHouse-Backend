package com.todayhouse.domain.review.api;

import com.todayhouse.domain.review.application.ReviewService;
import com.todayhouse.domain.review.dto.request.ReviewSaveRequest;
import com.todayhouse.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public BaseResponse saveReview(@RequestPart(value = "file", required = false) MultipartFile multipartFile,
                                   @RequestPart(value = "request") @Valid ReviewSaveRequest reviewSaveRequest) {
        Long saveId = reviewService.saveReview(multipartFile, reviewSaveRequest);
        return new BaseResponse(saveId);
    }


}


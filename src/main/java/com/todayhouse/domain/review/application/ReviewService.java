package com.todayhouse.domain.review.application;

import com.todayhouse.domain.review.dto.request.ReviewSaveRequest;
import org.springframework.web.multipart.MultipartFile;

public interface ReviewService {
    Long saveReview(MultipartFile multipartFile, ReviewSaveRequest request);
}

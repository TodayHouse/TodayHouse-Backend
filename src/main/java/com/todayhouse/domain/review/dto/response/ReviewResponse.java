package com.todayhouse.domain.review.dto.response;

import com.todayhouse.domain.product.dto.response.ProductResponse;
import com.todayhouse.domain.review.domain.Rating;
import com.todayhouse.domain.review.domain.Review;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewResponse {
    private Long id;
    private Long userId;
    private int like;
    private Rating rating;
    private String content;
    private String nickname;
    private String imageUrl;
    private ProductResponse productResponse;

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.userId = review.getUser().getId();
        this.rating = review.getRating();
        this.content = review.getContent();
        this.nickname = review.getContent();
        this.imageUrl = review.getReviewImage();
        this.productResponse = new ProductResponse(review.getProduct());
    }
}
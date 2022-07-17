package com.todayhouse.domain.review.dto.response;

import com.todayhouse.domain.product.dto.response.ProductResponse;
import com.todayhouse.domain.review.domain.Review;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewResponse {
    private Long id;
    private Long userId;
    private int like;
    private int rating;

    private boolean canLike;
    private String content;
    private String nickname;
    private String reviewImage;
    private String productImage;
    private String profileImage;
    private LocalDateTime createdAt;
    private ProductResponse productResponse;

    public ReviewResponse(Review review, boolean canLike) {
        this.id = review.getId();
        this.like = review.getLike();
        this.userId = review.getUser().getId();
        this.rating = review.getRating();
        this.content = review.getContent();
        this.canLike = canLike;
        this.nickname = review.getUser().getNickname();
        this.createdAt = review.getCreatedAt();
        this.reviewImage = review.getReviewImageUrl();
        this.productImage = review.getReviewImageUrl();
        this.profileImage = review.getUser().getProfileImage();
        this.productResponse = new ProductResponse(review.getProduct());
    }
}
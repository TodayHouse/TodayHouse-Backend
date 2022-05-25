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
    private boolean canLike;
    private Rating rating;
    private String content;
    private String nickname;
    private String productImage;
    private String profileImage;
    private ProductResponse productResponse;

    public ReviewResponse(Review review, boolean canLike) {
        this.id = review.getId();
        this.like = review.getLike();
        this.userId = review.getUser().getId();
        this.rating = review.getRating();
        this.content = review.getContent();
        this.canLike = canLike;
        this.nickname = review.getUser().getNickname();
        this.productImage = review.getReviewImage();
        this.profileImage = review.getUser().getProfileImage();
        this.productResponse = new ProductResponse(review.getProduct());
    }
}
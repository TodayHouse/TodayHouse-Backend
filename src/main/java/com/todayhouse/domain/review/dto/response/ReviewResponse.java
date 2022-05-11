package com.todayhouse.domain.review.dto.response;

import com.todayhouse.domain.product.dto.response.ProductResponse;
import com.todayhouse.domain.review.domain.Review;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewResponse {
    private Long id;
    private Long userId;
    private int liked;
    private int rating;
    private String content;
    private String nickname;
    private String profileImage;
    private ProductResponse productResponse;

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.userId = review.getUser().getId();
        this.liked = review.getLiked();
        this.rating = review.getRating();
        this.content = review.getContent();
        this.nickname = review.getContent();
        this.profileImage = review.getUser().getProfileImage();
        this.productResponse = new ProductResponse(review.getProduct());
    }
}
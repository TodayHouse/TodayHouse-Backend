package com.todayhouse.domain.review.dto.request;

import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.user.domain.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReviewSaveRequest {
    @Size(min = 1, max = 5, message = "rating은 1~5 사이의 숫자를 입력해주세요")
    private int rating;

    @NotNull(message = "productId를 입력해주세요")
    private Long productId;

    @NotBlank(message = "내용을 입력해주세요")
    private String content;

    public Review toEntity(String imageUrl, User user, Product product) {
        return Review.builder()
                .rating(rating)
                .content(content)
                .reviewImage(imageUrl)
                .user(user)
                .product(product).build();
    }
}

package com.todayhouse.domain.review.domain;

import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "liked")
    private int like = 0;

    private int rating;

    private String content;

    @Column(name = "review_image_url")
    private String reviewImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    public Review(int rating, String content, String reviewImage, User user, Product product) {
        this.rating = rating;
        this.content = content;
        this.reviewImageUrl = reviewImage;
        this.user = user;
        this.product = product;
    }

    public void updateUser(User user) {
        this.user = user;
    }

    public void updateProduct(Product product) {
        this.product = product;
    }

    public void updateReviewImageUrl(String reviewImage) {
        this.reviewImageUrl = reviewImage;
    }

    public void addLike() {
        like += 1;
    }

    public void subLike() {
        like -= 1;
    }
}

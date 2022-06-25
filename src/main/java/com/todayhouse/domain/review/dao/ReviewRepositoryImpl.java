package com.todayhouse.domain.review.dao;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.dto.request.ReviewSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

import static com.todayhouse.domain.review.domain.QReview.review;

public class ReviewRepositoryImpl extends QuerydslRepositorySupport
        implements CustomReviewRepository {

    public ReviewRepositoryImpl() {
        super(Review.class);
    }

    // 제품, 유저 id, 이미지 유무 별로 페이징
    @Override
    public Page<Review> findAllReviews(ReviewSearchRequest request, Pageable pageable) {
        JPQLQuery<Review> query = from(review)
                .innerJoin(review.user).fetchJoin()
                .innerJoin(review.product).fetchJoin();
        makeReviewSearchQuery(query, request);
        QueryResults<Review> results = getQuerydsl().applyPagination(pageable, query).fetchResults();

        List<Review> reviews = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(reviews, pageable, total);
    }

    // ReviewSearchRequest로 where절 추가
    private void makeReviewSearchQuery(JPQLQuery<Review> query, ReviewSearchRequest request) {
        if (request.getIsImage() != null && request.getIsImage()) {
            query.where(review.reviewImage.isNotEmpty());
        }
        if (request.getUserId() != null) {
            query.where(review.user.id.eq(request.getUserId()));
        }
        if (request.getProductId() != null) {
            query.where(review.product.id.eq(request.getProductId()));
        }
        if (request.getRating() != null) {
            query.where(review.rating.eq(request.getRating()));
        }
    }
}

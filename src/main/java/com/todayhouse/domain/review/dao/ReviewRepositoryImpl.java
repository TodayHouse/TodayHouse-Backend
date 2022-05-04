package com.todayhouse.domain.review.dao;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.todayhouse.domain.review.domain.QReview;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.dto.request.ReviewSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ReviewRepositoryImpl extends QuerydslRepositorySupport
        implements CustomReviewRepository {

    public ReviewRepositoryImpl() {
        super(Review.class);
    }

    // 제품, 유저 id, 이미지 유무 별로 페이징
    @Override
    public Page<Review> findAllReviews(ReviewSearchRequest request, Pageable pageable) {
        QReview qReview = QReview.review;
        JPQLQuery<Review> query = from(qReview)
                .innerJoin(qReview.user).fetchJoin()
                .innerJoin(qReview.product).fetchJoin();
        makeReviewSearchQuery(query, request);
        QueryResults<Review> results = getQuerydsl().applyPagination(pageable, query).fetchResults();

        List<Review> reviews = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(reviews, pageable, total);
    }

    // ReviewSearchRequest로 where절 추가
    private void makeReviewSearchQuery(JPQLQuery<Review> query, ReviewSearchRequest request) {
        QReview qReview = QReview.review;
        if (request.getIsImage() != null && request.getIsImage()) {
            query.where(qReview.reviewImage.isNotEmpty());
        }
        if (request.getUserId() != null) {
            query.where(qReview.user.id.eq(request.getUserId()));
        }
        if(request.getProductId() != null) {
            query.where(qReview.product.id.eq(request.getProductId()));
        }
        if (request.getRate() != null) {
            query.where(qReview.rating.eq(request.getRate()));
        }
    }
}

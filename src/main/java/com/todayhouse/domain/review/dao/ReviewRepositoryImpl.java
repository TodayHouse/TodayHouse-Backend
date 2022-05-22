package com.todayhouse.domain.review.dao;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.dto.request.ReviewSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.todayhouse.domain.review.domain.QReview.review;

public class ReviewRepositoryImpl extends QuerydslRepositorySupport
        implements CustomReviewRepository {

    public ReviewRepositoryImpl() {
        super(Review.class);
    }

    @Override
    public Page<Review> findAllReviews(ReviewSearchRequest request, Pageable pageable) {
        Set<Integer> ratings = splitToIntegerSet(request.getRatings());
        JPQLQuery<Review> query = from(review)
                .innerJoin(review.user).fetchJoin()
                .innerJoin(review.product).fetchJoin()
                .where(onlyImage(request.getOnlyImage()), eqUserId(request.getUserId()),
                        eqProductId(request.getProductId()), inTotalRating(ratings));
        List<Review> reviews = getQuerydsl().applyPagination(pageable, query).fetch();

        JPQLQuery<Review> countQuery = from(review)
                .innerJoin(review.user).fetchJoin()
                .innerJoin(review.product).fetchJoin()
                .where(onlyImage(request.getOnlyImage()), eqUserId(request.getUserId()),
                        eqProductId(request.getProductId()), inTotalRating(ratings));

        return PageableExecutionUtils.getPage(reviews, pageable, () -> countQuery.fetchCount());
    }

    private Set<Integer> splitToIntegerSet(String string) {
        if (ObjectUtils.isEmpty(string))
            return null;
        String[] tokens = string.split(",");
        Set<Integer> numbers = new HashSet<>();
        try {
            for (String token : tokens) {
                numbers.add(Integer.parseInt(token));
            }
        } catch (Exception e) {
            //1,2,3,4,5 형식
            throw new RuntimeException("정확한 평점을 입력해주세요.");
        }
        return numbers;
    }

    private BooleanExpression onlyImage(Boolean onlyImage) {
        if (onlyImage == null || !onlyImage)
            return null;
        return review.reviewImage.isNotEmpty();
    }

    private BooleanExpression eqUserId(Long userId) {
        if (userId == null)
            return null;
        return review.user.id.eq(userId);
    }

    private BooleanExpression eqProductId(Long productId) {
        if (productId == null)
            return null;
        return review.product.id.eq(productId);
    }

    private BooleanExpression inTotalRating(Set<Integer> ratings) {
        if (CollectionUtils.isEmpty(ratings))
            return null;
        return review.rating.total.in(ratings);
    }
}

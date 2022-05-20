package com.todayhouse.domain.review.dao;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.dto.request.ReviewSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
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

    // 제품, 유저 id, 이미지 유무 별로 페이징
    @Override
    public Page<Review> findAllReviews(ReviewSearchRequest request, Pageable pageable) {
        Set<Integer> ratings = splitToIntegerSet(request.getRatings());
        JPQLQuery<Review> query = from(review)
                .innerJoin(review.user).fetchJoin()
                .innerJoin(review.product).fetchJoin()
                .where(onlyImage(request.getOnlyImage()), eqUserId(request.getUserId()),
                        eqProductId(request.getProductId()), inTotalRating(ratings));
        QueryResults<Review> results = getQuerydsl().applyPagination(pageable, query).fetchResults();

        List<Review> reviews = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(reviews, pageable, total);
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
        if (onlyImage == null || onlyImage == false)
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
        System.out.println(ratings);
        if (ratings == null || ratings.isEmpty())
            return null;
        return review.rating.total.in(ratings);
    }
}

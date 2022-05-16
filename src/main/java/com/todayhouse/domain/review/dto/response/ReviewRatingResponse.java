package com.todayhouse.domain.review.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewRatingResponse {
    private long one = 0;
    private long two = 0;
    private long three = 0;
    private long four = 0;
    private long five = 0;
    private double average = 0;

    @Builder
    public ReviewRatingResponse(long one, long two, long three, long four, long five) {
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
        this.five = five;
        this.average = culcReviewAverage();
    }

    private double culcReviewAverage() {
        long total = this.getOne() + this.getTwo() * 2 + this.getThree() * 3 + this.getFour() * 4 + this.getFive() * 5;
        long count = this.getOne() + this.getTwo() + this.getThree() + this.getFour() + this.getFive();
        return Math.round((double) total / count * 10) / 10.0;
    }
}

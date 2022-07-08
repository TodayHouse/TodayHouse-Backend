package com.todayhouse.domain.inquiry.application;

import com.todayhouse.domain.inquiry.domain.Answer;

public interface AnswerService {
    Answer saveAnswer(Answer answer, Long productId, Long inquiryId);

    void deleteAnswer(Long answerId, Long productId);
}

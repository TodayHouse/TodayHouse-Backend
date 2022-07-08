package com.todayhouse.domain.inquiry.dao;

import com.todayhouse.domain.inquiry.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}

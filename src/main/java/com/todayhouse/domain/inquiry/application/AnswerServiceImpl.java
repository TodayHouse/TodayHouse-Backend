package com.todayhouse.domain.inquiry.application;

import com.todayhouse.domain.inquiry.dao.AnswerRepository;
import com.todayhouse.domain.inquiry.dao.InquiryRepository;
import com.todayhouse.domain.inquiry.domain.Answer;
import com.todayhouse.domain.inquiry.domain.Inquiry;
import com.todayhouse.domain.inquiry.exception.AnswerNotFoundException;
import com.todayhouse.domain.inquiry.exception.InquiryNotFoundException;
import com.todayhouse.domain.inquiry.exception.InvalidSellerAnswerException;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.exception.ProductNotFoundException;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final ProductRepository productRepository;
    private final InquiryRepository inquiryRepository;

    @Override
    public Answer saveAnswer(Answer answer, Long productId, Long inquiryId) {
        checkValidSeller(productId);
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(InquiryNotFoundException::new);
        Answer save = answerRepository.save(answer);
        inquiry.setAnswer(save);
        return save;
    }

    @Override
    public void deleteAnswer(Long answerId, Long productId) {
        checkValidSeller(productId);
        Answer answer = answerRepository.findById(answerId).orElseThrow(AnswerNotFoundException::new);
        answerRepository.delete(answer);
    }

    private void checkValidSeller(Long productId) {
        Product product = productRepository.findByIdWithSeller(productId).orElseThrow(ProductNotFoundException::new);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        if (product.getSeller() != user.getSeller())
            throw new InvalidSellerAnswerException();
    }
}

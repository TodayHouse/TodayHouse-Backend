package com.todayhouse.domain.inquiry.application;

import com.todayhouse.domain.inquiry.dao.InquiryRepository;
import com.todayhouse.domain.inquiry.domain.Inquiry;
import com.todayhouse.domain.inquiry.dto.request.InquirySearchRequest;
import com.todayhouse.domain.inquiry.exception.InquiryNotFoundException;
import com.todayhouse.domain.inquiry.exception.InvalidInquiryDeleteException;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.exception.ProductNotFoundException;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InquiryRepository inquiryRepository;

    @Override
    public Inquiry saveInquiry(Inquiry inquiry, Long productId) {
        User user = getValidUser();
        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        inquiry.setUser(user);
        inquiry.setProduct(product);
        return inquiryRepository.save(inquiry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Inquiry> findAllInquiries(InquirySearchRequest request, Pageable pageable) {
        return inquiryRepository.findAllInquiries(request, pageable);
    }

    @Override
    public void deleteInquiry(Long inquiryId) {
        User user = getValidUser();
        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(InquiryNotFoundException::new);
        if (inquiry.getUser() != user) {
            throw new InvalidInquiryDeleteException();
        }
        inquiryRepository.delete(inquiry);

        //answer 삭제 추가 구현
    }

    private User getValidUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }
}

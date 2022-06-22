package com.todayhouse.domain.inquiry.application;

import com.todayhouse.domain.inquiry.domain.Inquiry;
import com.todayhouse.domain.inquiry.dto.request.InquirySearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InquiryService {
    Inquiry saveInquiry(Inquiry inquiry, Long productId);
    Page<Inquiry> findAllInquiries(InquirySearchRequest request, Pageable pageable);
    void deleteInquiry(Long inquiryId);
}

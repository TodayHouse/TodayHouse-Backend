package com.todayhouse.domain.inquiry.dao;

import com.todayhouse.domain.inquiry.domain.Inquiry;
import com.todayhouse.domain.inquiry.dto.request.InquirySearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomInquiryRepository {
    Page<Inquiry> findAllInquiries(InquirySearchRequest request, Pageable pageable);
}

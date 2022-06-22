package com.todayhouse.domain.inquiry.dao;

import com.todayhouse.domain.inquiry.domain.Inquiry;
import com.todayhouse.domain.inquiry.dto.request.InquirySearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>, CustomInquiryRepository {
    Page<Inquiry> findAllInquiries(InquirySearchRequest request, Pageable pageable);
}

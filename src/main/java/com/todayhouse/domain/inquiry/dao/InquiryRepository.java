package com.todayhouse.domain.inquiry.dao;

import com.todayhouse.domain.inquiry.domain.Inquiry;
import com.todayhouse.domain.inquiry.dto.request.InquirySearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>, CustomInquiryRepository {
    Page<Inquiry> findAllInquiries(InquirySearchRequest request, Pageable pageable);

    @Query("select i from Inquiry i left join fetch i.answer where i.id=:inquiryId")
    Optional<Inquiry> findByIdWithAnswer(@Param("inquiryId") Long inquiryId);
}

package com.todayhouse.domain.inquiry.dao;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.todayhouse.domain.inquiry.domain.Inquiry;
import com.todayhouse.domain.inquiry.dto.InquirySearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.todayhouse.domain.inquiry.domain.QInquiry.inquiry;

public class InquiryRepositoryImpl extends QuerydslRepositorySupport
        implements CustomInquiryRepository {

    public InquiryRepositoryImpl() {
        super(Inquiry.class);
    }

    @Override
    public Page<Inquiry> findAllInquiries(InquirySearchRequest request, Pageable pageable) {
        List<Inquiry> inquiries = getPagingInquiries(request, pageable);

        JPQLQuery<Inquiry> countQuery = from(inquiry)
                .innerJoin(inquiry.user).fetchJoin()
                .innerJoin(inquiry.product).fetchJoin()
                .where(eqProductId(request.getProductId()), eqMyInquiry(request.getIsMyInquiry()));

        return PageableExecutionUtils.getPage(inquiries, pageable, () -> countQuery.fetchCount());
    }

    private List<Inquiry> getPagingInquiries(InquirySearchRequest request, Pageable pageable) {
        JPQLQuery<Long> idQuery = from(inquiry)
                .select(inquiry.id)
                .innerJoin(inquiry.user)
                .innerJoin(inquiry.product)
                .where(eqProductId(request.getProductId()), eqMyInquiry(request.getIsMyInquiry()));

        List<Long> ids = getQuerydsl().applyPagination(pageable, idQuery).fetch();

        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }

        JPQLQuery<Inquiry> query = from(inquiry)
                .innerJoin(inquiry.user).fetchJoin()
                .innerJoin(inquiry.product).fetchJoin()
                .leftJoin(inquiry.answer).fetchJoin()
                .where(inquiry.id.in(ids));

        return getQuerydsl().applySorting(pageable.getSort(), query).fetch();
    }

    private BooleanExpression eqProductId(Long productId) {
        if (productId == null)
            return null;
        return inquiry.product.id.eq(productId);
    }

    private BooleanExpression eqMyInquiry(Boolean isMyInquiry) {
        if (isMyInquiry == null || isMyInquiry == false)
            return null;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return inquiry.user.email.eq(email);
    }
}

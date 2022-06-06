package com.todayhouse.domain.inquiry.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.inquiry.domain.Answer;
import com.todayhouse.domain.inquiry.domain.Inquiry;
import com.todayhouse.domain.inquiry.dto.InquirySearchRequest;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InquiryRepositoryTest extends DataJpaBase {

    @Autowired
    TestEntityManager em;

    @Autowired
    InquiryRepository inquiryRepository;

    Product product1, product2;

    User user1, user2, user3, admin;

    @BeforeEach
    void setUp() {
        user1 = em.persist(User.builder().email("test1@test.com").nickname("test1").build());
        user2 = em.persist(User.builder().email("test2@test.com").nickname("test2").build());
        user3 = em.persist(User.builder().email("test3@test.com").nickname("test3").build());
        admin = em.persist(User.builder().email("admin@test.com").nickname("admin").build());
        product1 = em.persist(Product.builder().build());
        product2 = em.persist(Product.builder().build());
    }

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("상품 별 문의 페이징")
    void findAllInquiries() {
        Answer answer = em.persist(Answer.builder().content("answer").build());
        Inquiry inquiry1 = em.persist(Inquiry.builder().product(product1).user(user1).build());
        Inquiry inquiry2 = em.persist(Inquiry.builder().product(product1).user(user2).build());
        Inquiry inquiry3 = em.persist(Inquiry.builder().product(product1).user(user3).answer(answer).build());
        Inquiry inquiry4 = em.persist(Inquiry.builder().product(product2).user(admin).build());
        List<Long> ids = List.of(inquiry3.getId(), inquiry2.getId());

        InquirySearchRequest request = new InquirySearchRequest(product1.getId(), false);
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("createdAt").descending());

        Page<Inquiry> pages = inquiryRepository.findAllInquiries(request, pageRequest);
        List<Inquiry> content = pages.getContent();

        assertThat(pages.getTotalElements()).isEqualTo(3);
        assertThat(pages.getTotalPages()).isEqualTo(2);
        for (int i = 0; i < ids.size(); i++) {
            assertThat(ids.get(i)).isEqualTo(content.get(i).getId());
        }
        assertThat(content.get(0).getAnswer().getContent()).isEqualTo("answer");
    }

    @Test
    @DisplayName("자신의 상품 문의 페이징")
    void findAllInquiriesMyInquiries() {
        Answer answer = em.persist(Answer.builder().content("answer").build());
        Inquiry inquiry1 = em.persist(Inquiry.builder().product(product1).user(user1).build());
        Inquiry inquiry2 = em.persist(Inquiry.builder().product(product2).user(user1).answer(answer).build());
        Inquiry inquiry3 = em.persist(Inquiry.builder().product(product2).user(user2).build());
        List<Long> ids = List.of(inquiry2.getId(), inquiry1.getId());
        setSecurityName(user1.getEmail());

        InquirySearchRequest request = new InquirySearchRequest(null, true);
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("createdAt").descending());

        Page<Inquiry> pages = inquiryRepository.findAllInquiries(request, pageRequest);
        List<Inquiry> content = pages.getContent();

        assertThat(pages.getTotalElements()).isEqualTo(2);
        assertThat(pages.getTotalPages()).isEqualTo(1);
        for (int i = 0; i < ids.size(); i++) {
            assertThat(ids.get(i)).isEqualTo(content.get(i).getId());
        }
        assertThat(content.get(0).getAnswer().getContent()).isEqualTo("answer");
    }

    private void setSecurityName(String email) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }
}
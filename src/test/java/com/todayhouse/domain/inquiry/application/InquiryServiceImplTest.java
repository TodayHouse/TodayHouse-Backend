package com.todayhouse.domain.inquiry.application;

import com.todayhouse.domain.inquiry.dao.InquiryRepository;
import com.todayhouse.domain.inquiry.domain.Inquiry;
import com.todayhouse.domain.inquiry.dto.request.InquirySearchRequest;
import com.todayhouse.domain.inquiry.exception.InquiryNotFoundException;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.exception.ProductNotFoundException;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InquiryServiceImplTest {
    @InjectMocks
    InquiryServiceImpl inquiryService;

    @Mock
    UserRepository userRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    InquiryRepository inquiryRepository;

    @AfterEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("문의 저장")
    void saveInquiry() {
        Long productId = 1L;
        String email = "test@test";
        User user = mock(User.class);
        Product product = mock(Product.class);
        Inquiry inquiry = mock(Inquiry.class);
        setSecurityName(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(inquiryRepository.save(any(Inquiry.class))).thenReturn(inquiry);

        Inquiry result = inquiryService.saveInquiry(inquiry, productId);

        assertThat(result).isEqualTo(inquiry);
    }

    @Test
    @DisplayName("잘못된 유저 정보로 문의 저장")
    void saveInquiryUserNotFoundException() {
        Long productId = 1L;
        String email = "test@test";
        Inquiry inquiry = mock(Inquiry.class);
        setSecurityName(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(null));

        assertThrows(UserNotFoundException.class, () -> inquiryService.saveInquiry(inquiry, productId));
    }

    @Test
    @DisplayName("잘못된 상품 번호로 문의 저장")
    void saveInquiryNotFoundProductException() {
        Long productId = 1L;
        String email = "test@test";
        User user = mock(User.class);
        Inquiry inquiry = mock(Inquiry.class);
        setSecurityName(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.ofNullable(null));

        assertThrows(ProductNotFoundException.class, () -> inquiryService.saveInquiry(inquiry, productId));
    }


    @Test
    @DisplayName("페이징하여 문의 조회")
    void findAllInquiries() {
        InquirySearchRequest request = new InquirySearchRequest(1L, true);
        PageRequest page = PageRequest.of(0, 1);
        Page inquires = mock(Page.class);

        when(inquiryRepository.findAllInquiries(request, page)).thenReturn(inquires);

        Page<Inquiry> result = inquiryService.findAllInquiries(request, page);
        assertThat(result).isEqualTo(inquires);
    }

    @Test
    @DisplayName("문의 삭제")
    void deleteInquiry() {
        Inquiry inquiry = mock(Inquiry.class);

        when(inquiryRepository.findById(anyLong())).thenReturn(Optional.of(inquiry));
        doNothing().when(inquiryRepository).delete(inquiry);

        inquiryService.deleteInquiry(1L);
        verify(inquiryRepository).delete(any(Inquiry.class));
    }

    @Test
    @DisplayName("존재하지 않는 문의 삭제는 오류")
    void deleteInquiryNotFoundException() {
        when(inquiryRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        assertThrows(InquiryNotFoundException.class, () -> inquiryService.deleteInquiry(1L));
    }

    private void setSecurityName(String email) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }
}
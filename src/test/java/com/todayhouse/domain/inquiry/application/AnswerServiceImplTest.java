package com.todayhouse.domain.inquiry.application;

import com.todayhouse.domain.inquiry.dao.AnswerRepository;
import com.todayhouse.domain.inquiry.dao.InquiryRepository;
import com.todayhouse.domain.inquiry.domain.Answer;
import com.todayhouse.domain.inquiry.domain.Inquiry;
import com.todayhouse.domain.inquiry.exception.InvalidSellerAnswerException;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.exception.ProductNotFoundException;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerServiceImplTest {
    @InjectMocks
    AnswerServiceImpl answerService;
    @Mock
    UserRepository userRepository;
    @Mock
    AnswerRepository answerRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    InquiryRepository inquiryRepository;

    @Test
    @DisplayName("답변 저장")
    void saveAnswer() {
        String email = "test@test";
        Answer request = mock(Answer.class);
        Answer save = mock(Answer.class);
        Inquiry inquiry = Inquiry.builder().build();
        checkValidSeller(email);
        when(inquiryRepository.findById(anyLong())).thenReturn(Optional.of(inquiry));
        when(answerRepository.save(request)).thenReturn(save);

        Answer answer = answerService.saveAnswer(request, 1L, 1L);
        assertThat(answer).isEqualTo(save);
        assertThat(inquiry.getAnswer()).isEqualTo(save);
    }

    @Test
    @DisplayName("답변 삭제")
    void deleteAnswer() {
        String email = "test@test";
        checkValidSeller(email);
        when(answerRepository.findById(anyLong())).thenReturn(Optional.of(mock(Answer.class)));
        doNothing().when(answerRepository).delete(any(Answer.class));

        answerService.deleteAnswer(1L, 1L);
        verify(answerRepository).delete(any(Answer.class));
    }

    @Test
    @DisplayName("상품을 찾을 수 없음")
    void deleteAnswerProductNotFound() {
        when(productRepository.findByIdWithSeller(anyLong())).thenReturn(Optional.ofNullable(null));

        assertThrows(ProductNotFoundException.class, () -> answerService.deleteAnswer(1L, 1L));
    }

    @Test
    @DisplayName("유저를 찾을 수 없음")
    void deleteAnswerUserNotFound() {
        Product product = mock(Product.class);
        setSecurityName("test@test");
        when(productRepository.findByIdWithSeller(anyLong())).thenReturn(Optional.of(product));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(null));

        assertThrows(UserNotFoundException.class, () -> answerService.deleteAnswer(1L, 1L));
    }

    @Test
    @DisplayName("판매자가 아니면 답변을 할 수 없음")
    void deleteAnswerUserCannotAnswer() {
        String email = "test@test";
        Product product = mock(Product.class);
        User user = mock(User.class);
        Seller seller1 = mock(Seller.class);
        Seller seller2 = mock(Seller.class);
        setSecurityName(email);
        when(productRepository.findByIdWithSeller(anyLong())).thenReturn(Optional.of(product));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(user.getSeller()).thenReturn(seller1);
        when(product.getSeller()).thenReturn(seller2);

        assertThrows(InvalidSellerAnswerException.class, () -> answerService.deleteAnswer(1L, 1L));
    }

    private void setSecurityName(String email) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }

    private void checkValidSeller(String email) {
        User user = mock(User.class);
        Product product = mock(Product.class);
        Seller seller = mock(Seller.class);
        setSecurityName(email);
        when(productRepository.findByIdWithSeller(anyLong())).thenReturn(Optional.of(product));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(user.getSeller()).thenReturn(seller);
        when(product.getSeller()).thenReturn(seller);
    }
}
package com.todayhouse.domain.scrap.application;

import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.exception.ProductNotFoundException;
import com.todayhouse.domain.scrap.dao.ScrapRepository;
import com.todayhouse.domain.scrap.domain.Scrap;
import com.todayhouse.domain.scrap.exception.ScrapExistException;
import com.todayhouse.domain.scrap.exception.ScrapNotFoundException;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.UserNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScrapServiceImplTest {

    @InjectMocks
    ScrapServiceImpl scrapService;
    @Mock
    UserRepository userRepository;
    @Mock
    ScrapRepository scrapRepository;
    @Mock
    ProductRepository productRepository;

    @AfterEach
    void perSet() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("스크랩 저장")
    void saveScrap() {
        String email = "test";
        Scrap mockScrap = mock(Scrap.class);
        setSecurityName(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mock(User.class)));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(mock(Product.class)));
        when(scrapRepository.findByUserAndProduct(any(User.class), any(Product.class)))
                .thenReturn(Optional.ofNullable(null));
        when(scrapRepository.save(any(Scrap.class))).thenReturn(mockScrap);

        Scrap scrap = scrapService.saveScrap(1L);

        assertThat(scrap).isEqualTo(mockScrap);
    }

    @Test
    @DisplayName("입력받은 아이디의 상품을 찾을 수 없음")
    void scrapSaveNotFoundProduct() {
        String email = "test";
        setSecurityName(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mock(User.class)));
        when(productRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        assertThrows(ProductNotFoundException.class, () -> scrapService.saveScrap(1L));
    }

    @Test
    @DisplayName("이미 스크랩한 상품")
    void scrapSaveExist() {
        String email = "test";
        setSecurityName(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mock(User.class)));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(mock(Product.class)));
        when(scrapRepository.findByUserAndProduct(any(User.class), any(Product.class)))
                .thenReturn(Optional.ofNullable(mock(Scrap.class)));

        assertThrows(ScrapExistException.class, () -> scrapService.saveScrap(1L));
    }

    @Test
    @DisplayName("스크랩한 상품")
    void isScraped() {
        findScrapNullable(mock(Scrap.class));
        Boolean isScraped = scrapService.isScraped(1L);

        assertTrue(isScraped);
    }

    @Test
    @DisplayName("스크랩하지 않은 상품")
    void isNotScraped() {
        findScrapNullable(null);
        Boolean isScraped = scrapService.isScraped(1L);

        assertFalse(isScraped);
    }

    @Test
    @DisplayName("스크랩 삭제")
    void deleteScrap() {
        Scrap mockScrap = mock(Scrap.class);
        findScrapNullable(mockScrap);
        doNothing().when(scrapRepository).delete(mockScrap);

        scrapService.deleteScrap(1L);
    }

    @Test
    @DisplayName("존재하지 않는 스크랩 삭제")
    void deleteScrapNotExist() {
        findScrapNullable(null);

        assertThrows(ScrapNotFoundException.class, () -> scrapService.deleteScrap(1L));
    }

    @Test
    @DisplayName("product id로 스크랩 개수 세기")
    void countScrapByProductId() {
        Product mockProduct = mock(Product.class);
        Long count = 10L;
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(mockProduct));
        when(scrapRepository.countByProduct(mockProduct)).thenReturn(count);

        assertThat(scrapService.countScrapByProductId(1L)).isEqualTo(count);
    }

    @Test
    @DisplayName("존재하지 않는 product id로 스크랩 개수 세기")
    void countScrapByProductIdNotFoundProduct() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

        assertThrows(ProductNotFoundException.class, () -> scrapService.countScrapByProductId(1L));
    }

    @Test
    @DisplayName("자신의 스크랩 개수 세기")
    void countMyScrap() {
        String email = "test";
        Long count = 10L;
        User mockUser = mock(User.class);
        setSecurityName(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(scrapRepository.countByUser(mockUser)).thenReturn(count);

        assertThat(scrapService.countMyScrap()).isEqualTo(count);
    }

    @Test
    @DisplayName("존재하지 않는 유저")
    void notFountUser() {
        String email = "email";
        setSecurityName(email);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(null));

        assertThrows(UserNotFoundException.class, () -> scrapService.countMyScrap());
    }

    private void setSecurityName(String email) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }

    private void findScrapNullable(Scrap scrap) {
        String email = "test";
        setSecurityName(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mock(User.class)));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(mock(Product.class)));
        when(scrapRepository.findByUserAndProduct(any(User.class), any(Product.class)))
                .thenReturn(Optional.ofNullable(scrap));
    }
}
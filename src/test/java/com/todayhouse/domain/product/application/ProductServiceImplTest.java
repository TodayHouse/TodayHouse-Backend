package com.todayhouse.domain.product.application;

import com.todayhouse.domain.product.dao.CustomProductRepository;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.ProductSaveRequest;
import com.todayhouse.domain.product.dto.request.ProductUpdateRequest;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @InjectMocks
    ProductServiceImpl productService;

    @Mock
    UserRepository userRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    CustomProductRepository customProductRepository;

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("product 저장")
    void saveProduct() {
        String email = "test@test.com";
        SecurityContextSetting(email);
        Seller seller = Seller.builder().build();
        Product product = Product.builder().seller(seller).build();
        User user = User.builder().email(email).seller(seller).build();
        ProductSaveRequest request = ProductSaveRequest.builder().build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.saveProduct(request);
        assertThat(result).isEqualTo(product);
    }

    @Test
    void findAll() {
        Page<Product> products = Mockito.mock(Page.class);
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("createdAt").descending());

        when(customProductRepository.findAll(pageRequest)).thenReturn(products);

        Page<Product> result = productService.findAll(pageRequest);
        assertThat(result).isEqualTo(products);
    }

    @Test
    void findOne() {
        Seller seller = Seller.builder().build();
        Product product = Product.builder().seller(seller).build();
        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(product));

        Product result = productService.findOne(1L);
        assertThat(result).isEqualTo(product);
    }

    @Test
    void updateProduct() {
        ProductUpdateRequest request = ProductUpdateRequest.builder().id(1L).build();
        Product product = getValidProduct(request.getId());
        when(productRepository.save(product)).thenReturn(product);

        assertThat(productService.updateProduct(request)).isEqualTo(product);
    }

    @Test
    void removeProduct() {
        getValidProduct(1L);
        doNothing().when(productRepository).deleteById(1L);

        productService.removeProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    private void SecurityContextSetting(String email) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }

    private Product getValidProduct(Long id){
        String email = "email@test.com";
        SecurityContextSetting(email);
        User user = User.builder().email(email).build();
        Seller seller = Seller.builder().user(user).build();
        Product product = Product.builder().seller(seller).build();
        when(productRepository.findById(id)).thenReturn(Optional.ofNullable(product));

        return product;
    }

}
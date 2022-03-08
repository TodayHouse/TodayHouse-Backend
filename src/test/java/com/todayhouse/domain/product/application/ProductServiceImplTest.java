package com.todayhouse.domain.product.application;

import com.todayhouse.domain.category.dao.CategoryRepository;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.image.application.ImageService;
import com.todayhouse.domain.product.dao.CustomProductRepository;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.ChildOptionSaveRequest;
import com.todayhouse.domain.product.dto.request.ParentOptionSaveRequest;
import com.todayhouse.domain.product.dto.request.ProductSaveRequest;
import com.todayhouse.domain.product.dto.request.ProductUpdateRequest;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.infra.S3Storage.service.FileService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @InjectMocks
    ProductServiceImpl productService;

    @Mock
    FileService fileService;

    @Mock
    ImageService imageService;

    @Mock
    UserRepository userRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    CategoryRepository categoryRepository;

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
        MultipartFile multipartFile = new MockMultipartFile("data", "filename.txt", "text/plain", "bytes".getBytes());
        List<MultipartFile> list = new ArrayList<>();
        list.add(multipartFile);

        Set<ChildOptionSaveRequest> child = new LinkedHashSet<>();
        ChildOptionSaveRequest c1 = ChildOptionSaveRequest.builder().content("c1").build();
        ChildOptionSaveRequest c2 = ChildOptionSaveRequest.builder().content("c2").build();
        child.add(c1);
        child.add(c2);
        ParentOptionSaveRequest p1 = ParentOptionSaveRequest.builder().content("p1").childOptions(child).build();
        Set<ParentOptionSaveRequest> parent = new LinkedHashSet<>();
        parent.add(p1);
        ProductSaveRequest request = ProductSaveRequest.builder().categoryId(1L).parentOptions(parent).build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.ofNullable(Category.builder().build()));
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(fileService.upload(list)).thenReturn(List.of("data"));
        doNothing().when(imageService).save(anyList(), any(Product.class));

        Long id = productService.saveProductRequest(list, request);
        assertThat(id).isEqualTo(product.getId());
    }

//    @Test
//    void findAll() {
//        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("createdAt").descending());
//        Page<ProductResponse> response = Mockito.spy(Page.class);
//        Page<Product> products = Mockito.spy(Page.class);
//
//        when(customProductRepository.findAll(pageRequest)).thenReturn(products);
//
//        Page<ProductResponse> result = productService.findAll(pageRequest);
//        assertThat(result).isEqualTo(response);
//    }

    @Test
    @DisplayName("product id로 찾기")
    void findOne() {
        Seller seller = Seller.builder().build();
        Product product = Product.builder().seller(seller).build();
        when(productRepository.findByIdWithImages(1L)).thenReturn(Optional.ofNullable(product));

        Product result = productService.findByIdWithImages(1L);
        assertThat(result).isEqualTo(product);
    }

    @Test
    @DisplayName("product 수정")
    void updateProduct() {
        ProductUpdateRequest request = ProductUpdateRequest.builder().id(1L).categoryId(1L).build();
        Product product = getValidProduct(request.getId());
        when(categoryRepository.findById(1L)).thenReturn(Optional.ofNullable(Category.builder().build()));
        when(productRepository.save(product)).thenReturn(product);

        assertThat(productService.updateProduct(request)).isEqualTo(product);
    }

    @Test
    @DisplayName("product id로 삭제")
    void removeProduct() {
        getValidProduct(1L);
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    private void SecurityContextSetting(String email) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }

    private Product getValidProduct(Long id) {
        String email = "email@test.com";
        SecurityContextSetting(email);
        Seller seller = Seller.builder().build();
        User user = User.builder().email(email).seller(seller).build();
        Product product = Product.builder().seller(seller).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));
        when(productRepository.findByIdWithOptionsAndSeller(id)).thenReturn(Optional.ofNullable(product));

        return product;
    }

}
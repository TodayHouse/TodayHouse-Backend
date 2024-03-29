package com.todayhouse.domain.product.application;

import com.todayhouse.domain.category.dao.CategoryRepository;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.image.application.ImageService;
import com.todayhouse.domain.image.dao.ProductImageRepository;
import com.todayhouse.domain.image.domain.ProductImage;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.*;
import com.todayhouse.domain.product.dto.response.ProductResponse;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.exception.InvalidRequestException;
import com.todayhouse.domain.user.exception.SellerNotFoundException;
import com.todayhouse.infra.S3Storage.service.FileService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    ProductImageRepository productImageRepository;

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

        Set<ProductChildOptionSaveRequest> child = new LinkedHashSet<>();
        ProductChildOptionSaveRequest c1 = ProductChildOptionSaveRequest.builder().content("c1").build();
        ProductChildOptionSaveRequest c2 = ProductChildOptionSaveRequest.builder().content("c2").build();
        child.add(c1);
        child.add(c2);
        ProductParentOptionSaveRequest p1 = ProductParentOptionSaveRequest.builder().content("p1").childOptions(child).build();
        Set<ProductParentOptionSaveRequest> parent = new LinkedHashSet<>();
        parent.add(p1);
        ProductSaveRequest request = ProductSaveRequest.builder().categoryName("가전").parentOptions(parent).build();

        when(categoryRepository.findByName(anyString())).thenReturn(Optional.ofNullable(Category.builder().build()));
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(fileService.uploadImages(list)).thenReturn(List.of("data"));
        doNothing().when(imageService).save(anyList(), any(Product.class));

        Long id = productService.saveProductRequest(list, request);
        assertThat(id).isEqualTo(product.getId());
    }

    @Test
    @DisplayName("product 저장 시 seller null 오류")
    void saveProductSellerNullException() {
        String email = "test@test.com";
        SecurityContextSetting(email);
        User user = User.builder().email(email).build();
        MultipartFile multipartFile = new MockMultipartFile("data", "filename.txt", "text/plain", "bytes".getBytes());
        List<MultipartFile> list = new ArrayList<>();
        list.add(multipartFile);

        ProductSaveRequest request = ProductSaveRequest.builder().categoryName("가전").build();

        when(categoryRepository.findByName(anyString())).thenReturn(Optional.ofNullable(Category.builder().build()));
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));

        assertThrows(SellerNotFoundException.class, () -> productService.saveProductRequest(list, request));
    }

    @Test
    @DisplayName("이미지 없이 product 저장")
    void saveProductWithoutImage() {
        String email = "test@test.com";
        SecurityContextSetting(email);
        Seller seller = Seller.builder().build();
        Product product = Product.builder().seller(seller).build();
        User user = User.builder().email(email).seller(seller).build();

        ProductParentOptionSaveRequest p1 = ProductParentOptionSaveRequest.builder().content("p1").build();
        Set<ProductParentOptionSaveRequest> parent = new LinkedHashSet<>();
        parent.add(p1);
        ProductSaveRequest request = ProductSaveRequest.builder().categoryName("가전").parentOptions(parent).build();

        when(categoryRepository.findByName("가전")).thenReturn(Optional.ofNullable(Category.builder().build()));
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        doNothing().when(imageService).save(any(), any(Product.class));

        Long id = productService.saveProductRequest(new ArrayList<>(), request);
        assertThat(id).isEqualTo(product.getId());
    }

    @Test
    @DisplayName("product 페이징 조회")
    void findAll() {
        Seller seller = Seller.builder().brand("test").build();
        ProductSearchRequest search = ProductSearchRequest.builder().build();
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("id").descending());
        Product product1 = Product.builder().image("img").seller(seller).build();
        Product product2 = Product.builder().image("img").seller(seller).build();
        List<Product> productList = List.of(product1, product2);
        Page<Product> products = new PageImpl<>(productList, pageRequest, 2);

        when(productRepository.findAllWithSeller(search, pageRequest)).thenReturn(products);
        when(fileService.changeFileNameToUrl(anyString())).thenReturn("s3.img.com");

        Page<ProductResponse> result = productService.findAllWithSeller(search, pageRequest);
        List<ProductResponse> contents = result.getContent();
        assertThat(contents.size()).isEqualTo(2);
        assertThat(contents.get(0).getBrand()).isEqualTo("test");
        assertThat(contents.get(0).getImageUrls()).isEqualTo(List.of("s3.img.com"));
    }

    @Test
    @DisplayName("product id로 찾기")
    void findOne() {
        Seller seller = Seller.builder().build();
        Product product = Product.builder().seller(seller).image("aa.jpg").build();
        when(productRepository.findByIdWithOptionsAndSeller(1L)).thenReturn(Optional.ofNullable(product));

        Product result = productService.findByIdWithOptionsAndSeller(1L);
        assertThat(result).isEqualTo(product);
    }

    @Test
    @DisplayName("product 수정")
    void updateProduct() {
        ProductUpdateRequest request = ProductUpdateRequest.builder().id(1L).categoryName("가구").build();
        Product product = getValidProduct(request.getId());
        when(categoryRepository.findByName("가구")).thenReturn(Optional.ofNullable(Category.builder().build()));
        when(productRepository.save(product)).thenReturn(product);

        assertThat(productService.updateProduct(request)).isEqualTo(product);
    }

    @Test
    @DisplayName("product를 id로 삭제")
    void deleteProduct() {
        ProductImage img1 = ProductImage.builder().product(mock(Product.class)).fileName("img1").build();
        ProductImage img2 = ProductImage.builder().product(mock(Product.class)).fileName("img2").build();

        getValidProduct(1L);
        doNothing().when(productRepository).deleteById(1L);
        when(productImageRepository.findByProductId(1L)).thenReturn(List.of(img1, img2));
        doNothing().when(fileService).delete(anyList());
        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
        verify(productImageRepository).findByProductId(1L);
        verify(fileService).delete(anyList());
    }

    @Test
    @DisplayName("product를 seller가 아닌 사람이 삭제")
    void deleteProductNotSellerExecption() {
        String email = "email@test.com";
        SecurityContextSetting(email);
        Seller seller1 = Seller.builder().build();
        Seller seller2 = Seller.builder().build();
        User user = User.builder().email(email).seller(seller1).build();
        Product product = Product.builder().seller(seller2).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(user));
        when(productRepository.findByIdWithSeller(1L)).thenReturn(Optional.ofNullable(product));

        assertThrows(InvalidRequestException.class, () -> productService.deleteProduct(1L));
    }

    @Test
    @DisplayName("파일 저장")
    void saveFiles() {
        MockMultipartFile file = new MockMultipartFile("file", "image.jpa", "image/jpeg", "<<jpeg data>>".getBytes(StandardCharsets.UTF_8));

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
        when(productRepository.findByIdWithSeller(id)).thenReturn(Optional.ofNullable(product));

        return product;
    }

}
package com.todayhouse.domain.product.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.category.dao.CategoryRepository;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.image.dao.ProductImageRepository;
import com.todayhouse.domain.image.domain.ProductImage;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.ChildOption;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.*;
import com.todayhouse.domain.product.dto.response.ProductResponse;
import com.todayhouse.domain.product.dto.response.ProductSearchResponse;
import com.todayhouse.domain.user.dao.SellerRepository;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import com.todayhouse.infra.S3Storage.service.FileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest extends IntegrationBase {

    @Autowired
    UserRepository userRepository;

    @Autowired
    SellerRepository sellerRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @PersistenceContext
    EntityManager em;

    @MockBean
    FileServiceImpl fileService;

    Seller seller1;
    Product product1;

    @BeforeEach
    void setUp() {
        seller1 = Seller.builder().email("seller1@email.com").brand("user1").build();
        User user1 = User.builder().email("user1@email.com").seller(seller1).build();
        userRepository.save(user1);
        product1 = Product.builder().title("p1").seller(seller1).build();
        productRepository.save(product1);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("product와 image 저장")
    void saveProduct() throws Exception {
        String url = "http://localhost:8080/products";
        String jwt = jwtTokenProvider.createToken("user1@email.com", Collections.singletonList(Role.USER));
        Set<ProductChildOptionSaveRequest> child = new LinkedHashSet<>();
        ProductChildOptionSaveRequest c1 = ProductChildOptionSaveRequest.builder().content("c1").build();
        ProductChildOptionSaveRequest c2 = ProductChildOptionSaveRequest.builder().content("c2").build();
        child.add(c1);
        child.add(c2);
        ProductParentOptionSaveRequest p1 = ProductParentOptionSaveRequest.builder().content("p1").childOptions(child).build();
        Set<ProductParentOptionSaveRequest> parent = new LinkedHashSet<>();
        parent.add(p1);
        ProductSaveRequest request = ProductSaveRequest.builder()
                .title("new").price(10000).deliveryFee(1000).discountRate(10).specialPrice(false).categoryId(1L)
                .parentOptions(parent).build();
        MockMultipartFile json = new MockMultipartFile("request", "json", "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));
        MockMultipartFile image = new MockMultipartFile("file", "image.jpa", "image/jpeg", "<<jpeg data>>".getBytes(StandardCharsets.UTF_8));

        when(fileService.uploadImages(anyList())).thenReturn(List.of("filename-1.jpeg"));

        MvcResult mvcResult = mockMvc.perform(multipart(url)
                        .file(image)
                        .file(json)
                        .contentType("multipart/mixed")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        Long id = ((Number) objectMapper.convertValue(getResponseFromMvcResult(mvcResult).getResult(), Map.class).get("productId")).longValue();
        Product product = productRepository.findByIdWithOptionsAndSeller(id).orElse(null);
        ProductImage productImage = productImageRepository.findFirstByProductOrderByCreatedAtAsc(product).orElse(null);
        Set<ParentOption> parents = product.getParents();
        List<ParentOption> list = new ArrayList<>(parents);
        Set<ChildOption> children = list.get(0).getChildren();
        assertThat(product.getTitle()).isEqualTo("new");
        assertThat(product.getImage()).isEqualTo("filename-1.jpeg");
        assertThat(list.get(0).getContent()).isEqualTo("p1");
        assertThat(list.size()).isEqualTo(1);
        assertThat(children.size()).isEqualTo(2);
        assertThat(productImage).isNotNull();
    }

    @Test
    @DisplayName("jwt가 없으면 product 저장 불가")
    void saveNotLogin() throws Exception {
        String url = "http://localhost:8080/products";
        Set<ProductChildOptionSaveRequest> child = new LinkedHashSet<>();
        ProductChildOptionSaveRequest c1 = ProductChildOptionSaveRequest.builder().content("c1").build();
        ProductChildOptionSaveRequest c2 = ProductChildOptionSaveRequest.builder().content("c2").build();
        child.add(c1);
        child.add(c2);
        ProductParentOptionSaveRequest p1 = ProductParentOptionSaveRequest.builder().content("p1").childOptions(child).build();
        Set<ProductParentOptionSaveRequest> parent = new LinkedHashSet<>();
        parent.add(p1);
        ProductSaveRequest request = ProductSaveRequest.builder()
                .title("new").price(10000).deliveryFee(1000).discountRate(10).specialPrice(false).categoryId(1L)
                .parentOptions(parent).build();
        MockMultipartFile json = new MockMultipartFile("request", "json", "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));
        MockMultipartFile image = new MockMultipartFile("file", "image.jpa", "image/jpeg", "<<jpeg data>>".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart(url)
                        .file(image)
                        .file(json)
                        .contentType("multipart/mixed")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("product id 오름차순 페이징")
    void findProductsPaginationASC() throws Exception {
        String url = "http://localhost:8080/products?page=0&size=2&sort=id,ASC";
        productRepository.save(Product.builder().seller(seller1).build());
        productRepository.save(Product.builder().seller(seller1).build());
        productRepository.save(Product.builder().seller(seller1).build());

        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse baseResponse = getResponseFromMvcResult(mvcResult);
        ProductSearchResponse response = objectMapper.readValue(objectMapper.writeValueAsString(baseResponse.getResult()), new TypeReference<>() {
        });
        List<ProductResponse> products = objectMapper.readValue(objectMapper.writeValueAsString(response.getContent()), new TypeReference<>() {
        });
        assertThat(response.getTotalPages()).isEqualTo(2);
        assertThat(response.getTotalElements()).isEqualTo(4);
        Long tmp = 0L;
        for (ProductResponse p : products) {
            assertThat(p.getId()).isGreaterThanOrEqualTo(tmp);
            tmp = p.getId();
        }
    }

    @Test
    @DisplayName("product price, id 내림차순 페이징")
    void findProductsPaginationDesc() throws Exception {
        String url = "http://localhost:8080/products?page=0&size=4&sort=price,DESC&sort=id,DESC";
        productRepository.save(Product.builder().price(100).seller(seller1).build());
        productRepository.save(Product.builder().price(2000).seller(seller1).build());
        productRepository.save(Product.builder().price(100).seller(seller1).build());

        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse baseResponse = getResponseFromMvcResult(mvcResult);
        ProductSearchResponse response = objectMapper.readValue(objectMapper.writeValueAsString(baseResponse.getResult()), new TypeReference<>() {
        });
        List<ProductResponse> products = objectMapper.readValue(objectMapper.writeValueAsString(response.getContent()), new TypeReference<>() {
        });
        assertThat(response.getTotalPages()).isEqualTo(1);
        assertThat(response.getTotalElements()).isEqualTo(4);
        int price = 10000;
        for (ProductResponse p : products) {
            assertThat(p.getPrice()).isLessThanOrEqualTo(price);
            price = p.getPrice();
        }
    }

    @Test
    @DisplayName("product 찾았다")
    void findProduct() throws Exception {
        String imageUrl = "saveUrl";
        ProductImage img1 = ProductImage.builder().product(product1).fileName("test1.jpg").build();
        ProductImage img2 = ProductImage.builder().product(product1).fileName("test2.jpg").build();
        productImageRepository.save(img1);
        productImageRepository.save(img2);
        product1.updateImage(imageUrl);
        productRepository.save(product1);
        em.flush();
        em.clear();

        when(fileService.changeFileNameToUrl(anyString())).thenReturn(imageUrl);

        String url = "http://localhost:8080/products/" + product1.getId();
        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse baseResponse = getResponseFromMvcResult(mvcResult);
        ProductResponse result = objectMapper.convertValue(baseResponse.getResult(), ProductResponse.class);
        assertThat(result.getTitle()).isEqualTo("p1");
        assertThat(result.getImageUrls().get(0)).isEqualTo(imageUrl);
        assertThat(result.getImageUrls().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("product 수정")
    void updateProduct() throws Exception {
        String url = "http://localhost:8080/products";
        String jwt = jwtTokenProvider.createToken("user1@email.com", Collections.singletonList(Role.USER));
        ProductUpdateRequest request = ProductUpdateRequest.builder().id(product1.getId()).title("new").categoryId(2L).build();

        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andDo(print());

        List<Product> products = productRepository.findAll();
        assertThat(products.size()).isEqualTo(1);
        assertThat(products.get(0).getTitle()).isEqualTo("new");
    }

    @Test
    @DisplayName("product 삭제")
    void deleteProduct() throws Exception {
        String fileName = "test/jpg";
        ProductImage img = ProductImage.builder().product(product1).fileName(fileName).build();
        productImageRepository.save(img);
        em.flush();
        em.clear();

        String url = "http://localhost:8080/products/" + product1.getId();
        String jwt = jwtTokenProvider.createToken("user1@email.com", Collections.singletonList(Role.USER));
        doNothing().when(fileService).deleteOne(fileName);

        mockMvc.perform(delete(url)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());

        List<Product> products = productRepository.findAll();
        List<ProductImage> images = productImageRepository.findAll();
        for (Product p : products) {
            System.out.println(p.toString());
        }
        assertThat(products.size()).isEqualTo(0);
        assertThat(images.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("배달비 존재, 특가 product를 id 내림차순 페이징")
    void findProductsPaginationBrand() throws Exception {
        String url = "http://localhost:8080/products?page=0&size=4&sort=id,DESC";
        ProductSearchRequest productSearch = ProductSearchRequest.builder().specialPrice(true).deliveryFee(true).build();
        productRepository.save(Product.builder().specialPrice(true).deliveryFee(2000).seller(seller1).build());
        productRepository.save(Product.builder().specialPrice(false).deliveryFee(1000).seller(seller1).build());
        productRepository.save(Product.builder().specialPrice(true).seller(seller1).build());

        MvcResult mvcResult = mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productSearch)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse baseResponse = getResponseFromMvcResult(mvcResult);
        ProductSearchResponse response = objectMapper.readValue(objectMapper.writeValueAsString(baseResponse.getResult()), new TypeReference<>() {
        });
        List<ProductResponse> products = objectMapper.readValue(objectMapper.writeValueAsString(response.getContent()), new TypeReference<>() {
        });
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(products.get(0).getDeliveryFee()).isEqualTo(2000);
        assertThat(products.get(0).isSpecialPrice()).isTrue();
    }

    @Test
    @DisplayName("하위카테고리 제품 찾기")
    void getSubAll() throws Exception {
        Category air = categoryRepository.findByName("에어컨").orElse(null);
        Category laptop = categoryRepository.findByName("노트북").orElse(null);
        Category desktop = categoryRepository.findByName("컴퓨터").orElse(null);
        productRepository.save(Product.builder().title("air").category(air).seller(seller1).build());
        productRepository.save(Product.builder().title("lap").category(laptop).seller(seller1).build());
        productRepository.save(Product.builder().title("desk").category(desktop).seller(seller1).build());

        String url = "http://localhost:8080/products";
        Long id = categoryRepository.findByName("컴퓨터/노트북").orElse(null).getId();
        ProductSearchRequest productSearch = ProductSearchRequest.builder().categoryId(id).build();

        MvcResult mvcResult = mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productSearch)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse baseResponse = getResponseFromMvcResult(mvcResult);
        ProductSearchResponse response = objectMapper.readValue(objectMapper.writeValueAsString(baseResponse.getResult()), new TypeReference<>() {
        });
        List<ProductResponse> products = objectMapper.readValue(objectMapper.writeValueAsString(response.getContent()), new TypeReference<>() {
        });
        assertThat(products.size()).isEqualTo(2);
        assertTrue(products.stream().anyMatch(p -> p.getTitle().equals("lap")));
        assertTrue(products.stream().anyMatch(p -> p.getTitle().equals("desk")));
    }

    @Test
    @DisplayName("product image 삭제 후 대표이미지 변경")
    void deleteProductImage() throws Exception {
        String fileName1 = "test1.jpg";
        String fileName2 = "test2.jpg";
        ProductImage img1 = ProductImage.builder().product(product1).fileName(fileName1).build();
        ProductImage img2 = ProductImage.builder().product(product1).fileName(fileName2).build();
        productImageRepository.save(img1);
        productImageRepository.save(img2);
        em.flush();
        em.clear();

        String url = "http://localhost:8080/products/images/" + fileName1;
        String jwt = jwtTokenProvider.createToken("user1@email.com", Collections.singletonList(Role.USER));
        doNothing().when(fileService).deleteOne(fileName1);

        mockMvc.perform(delete(url)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());

        Product product = productRepository.findById(product1.getId()).orElse(null);
        List<ProductImage> images = productImageRepository.findByProductId(product1.getId());
        assertThat(images.get(0).getFileName()).isEqualTo(product.getImage());
    }

    @Test
    @DisplayName("사진 저장 후 삭제 시 다른 대표 사진으로 업데이트")
    void AddAndDeleteAndUpdateImage() throws Exception {
        MockMultipartFile first = new MockMultipartFile("file", "first.jpeg", "image/jpeg", "<<jpeg data1>>".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile second = new MockMultipartFile("file", "second.jpeg", "image/jpeg", "<<jpeg data2>>".getBytes(StandardCharsets.UTF_8));

        ProductImageSaveRequest request = ProductImageSaveRequest.builder().productId(product1.getId()).build();
        MockMultipartFile json = new MockMultipartFile("request", "json", "application/json", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));

        String jwt = jwtTokenProvider.createToken("user1@email.com", Collections.singletonList(Role.USER));

        when(fileService.uploadImages(anyList())).thenReturn(List.of("first.jpeg", "second.jpeg"));
        doNothing().when(fileService).delete(anyList());

        mockMvc.perform(multipart("http://localhost:8080/products/images")
                        .file(first)
                        .file(second)
                        .file(json)
                        .contentType("multipart/mixed")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andDo(print());
        String oldFile = productRepository.findById(product1.getId()).orElse(null).getImage();

        mockMvc.perform(delete("http://localhost:8080/products/images/" + oldFile)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andDo(print());
        String newFile = productRepository.findById(product1.getId()).orElse(null).getImage();

        assertThat(oldFile).isEqualTo("first.jpeg");
        assertThat(newFile).isEqualTo("second.jpeg");
    }

    public static class CustomPageImpl<T> extends PageImpl<T> {
        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public CustomPageImpl(@JsonProperty("content") List<T> content,
                              @JsonProperty("number") int number,
                              @JsonProperty("size") int size,
                              @JsonProperty("totalElements") Long totalElements,
                              @JsonProperty("pageable") JsonNode pageable,
                              @JsonProperty("last") boolean last,
                              @JsonProperty("totalPages") int totalPages,
                              @JsonProperty("sort") JsonNode sort,
                              @JsonProperty("first") boolean first,
                              @JsonProperty("numberOfElements") int numberOfElements) {

            super(content, PageRequest.of(number, size), totalElements);
        }

        public CustomPageImpl(List<T> content, Pageable pageable, long total) {
            super(content, pageable, total);
        }

        public CustomPageImpl(List<T> content) {
            super(content);
        }

        public CustomPageImpl() {
            super(new ArrayList<>());
        }
    }
}
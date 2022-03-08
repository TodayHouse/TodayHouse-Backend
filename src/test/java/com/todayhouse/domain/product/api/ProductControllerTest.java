package com.todayhouse.domain.product.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.category.dao.CategoryRepository;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.ProductSaveRequest;
import com.todayhouse.domain.product.dto.request.ProductSearchRequest;
import com.todayhouse.domain.product.dto.request.ProductUpdateRequest;
import com.todayhouse.domain.product.dto.response.ProductResponse;
import com.todayhouse.domain.product.dto.response.ProductSearchResponse;
import com.todayhouse.domain.user.dao.SellerRepository;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)//@BeforeAll 사용
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
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @BeforeAll
    void setUp() {
        Seller seller1 = Seller.builder().email("seller1@email.com").brand("user1").build();
        User user1 = User.builder().email("user1@email.com").seller(seller1).build();
        userRepository.save(user1);
        Product product1 = Product.builder().title("p1").seller(seller1).build();
        productRepository.save(product1);
    }


    @Test
    @DisplayName("product 저장")
    void saveProduct() throws Exception {
        String url = "http://localhost:8080/products";
        String jwt = jwtTokenProvider.createToken("user1@email.com", Collections.singletonList(Role.USER));
        ProductSaveRequest request = ProductSaveRequest.builder()
                .title("new").price(10000).deliveryFee(1000).discountRate(10).specialPrice(false).categoryId(1L)
                .build();

        MvcResult mvcResult = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        Long id = objectMapper.convertValue(getResponseFromMvcResult(mvcResult).getResult(), Long.class);
        Product product = productRepository.findById(id).orElse(null);
        assertThat(product.getTitle()).isEqualTo("new");
    }

    @Test
    @DisplayName("product id 오름차순 페이징")
    void findProductsPaginationASC() throws Exception {
        String url = "http://localhost:8080/products?page=0&size=2&sort=id,ASC";
        Seller seller = sellerRepository.findById(1L).orElse(null);
        productRepository.save(Product.builder().seller(seller).build());
        productRepository.save(Product.builder().seller(seller).build());
        productRepository.save(Product.builder().seller(seller).build());

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
            assertThat(p.getId()).isGreaterThan(tmp);
            tmp = p.getId();
        }
    }

    @Test
    @DisplayName("product price, id 내림차순 페이징")
    void findProductsPaginationDesc() throws Exception {
        String url = "http://localhost:8080/products?page=0&size=4&sort=price,DESC&sort=id,DESC";
        Seller seller = sellerRepository.findById(1L).orElse(null);
        productRepository.save(Product.builder().price(100).seller(seller).build());
        productRepository.save(Product.builder().price(2000).seller(seller).build());
        productRepository.save(Product.builder().price(100).seller(seller).build());

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
        String url = "http://localhost:8080/products/1";
        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse baseResponse = getResponseFromMvcResult(mvcResult);
        Product product = objectMapper.convertValue(baseResponse.getResult(), Product.class);
        assertThat(product.getTitle()).isEqualTo("p1");
    }

    @Test
    @DisplayName("product 수정")
    void updateProduct() throws Exception {
        String url = "http://localhost:8080/products";
        String jwt = jwtTokenProvider.createToken("user1@email.com", Collections.singletonList(Role.USER));
        ProductUpdateRequest request = ProductUpdateRequest.builder().id(1L).title("new").categoryId(2L).build();

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
        String url = "http://localhost:8080/products/1";
        String jwt = jwtTokenProvider.createToken("user1@email.com", Collections.singletonList(Role.USER));

        mockMvc.perform(delete(url)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());

        List<Product> products = productRepository.findAll();
        assertThat(products.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("배달비 존재, 특가 product를 id 내림차순 페이징")
    void findProductsPaginationBrand() throws Exception {
        String url = "http://localhost:8080/products?page=0&size=4&sort=id,DESC";
        ProductSearchRequest productSearch = ProductSearchRequest.builder().specialPrice(true).deliveryFee(true).build();
        Seller seller = sellerRepository.findById(1L).orElse(null);
        productRepository.save(Product.builder().specialPrice(true).deliveryFee(2000).seller(seller).build());
        productRepository.save(Product.builder().specialPrice(false).deliveryFee(1000).seller(seller).build());
        productRepository.save(Product.builder().specialPrice(true).seller(seller).build());

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
        Seller seller = sellerRepository.findById(1L).orElse(null);
        productRepository.save(Product.builder().title("air").category(air).seller(seller).build());
        productRepository.save(Product.builder().title("lap").category(laptop).seller(seller).build());
        productRepository.save(Product.builder().title("desk").category(desktop).seller(seller).build());

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
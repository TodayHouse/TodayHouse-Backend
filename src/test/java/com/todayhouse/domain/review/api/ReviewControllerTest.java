package com.todayhouse.domain.review.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.order.dao.OrderRepository;
import com.todayhouse.domain.order.domain.Orders;
import com.todayhouse.domain.order.domain.Status;
import com.todayhouse.domain.product.dao.ParentOptionRepository;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.review.dao.ReviewRepository;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.dto.request.ReviewSaveRequest;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import com.todayhouse.infra.S3Storage.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReviewControllerTest extends IntegrationBase {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    ReviewController reviewController;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ParentOptionRepository parentOptionRepository;

    @MockBean
    FileService fileService;

    User user1;
    Orders order1;
    Product product1;
    ParentOption option1;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(User.builder().email("test@test").build());
        product1 = productRepository.save(Product.builder().build());
        option1 = parentOptionRepository.save(ParentOption.builder().product(product1).price(9900).stock(10).build());
        order1 = Orders.builder().product(product1).user(user1).parentOption(option1).productQuantity(1).build();
        order1.updateStatus(Status.COMPLETED);
        order1 = orderRepository.save(order1);

    }

    @Test
    @DisplayName("사진과 리뷰 저장")
    void saveReviewWithImage() throws Exception {
        String url = "http://localhost:8080/reviews";
        String jwt = tokenProvider.createToken(user1.getEmail(), List.of(Role.USER));
        ReviewSaveRequest reviewSaveRequest = new ReviewSaveRequest(5, product1.getId(), "Good!");
        MockMultipartFile json =
                new MockMultipartFile("request", "json", "application/json", objectMapper.writeValueAsString(reviewSaveRequest).getBytes(StandardCharsets.UTF_8));
        MockMultipartFile image =
                new MockMultipartFile("file", "image.jpa", "image/jpeg", "<<jpeg data>>".getBytes(StandardCharsets.UTF_8));

        saveMultipartFile();

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
        BaseResponse response = getResponseFromMvcResult(mvcResult);

        Review review = reviewRepository.findByUserIdAndProductId(user1.getId(), product1.getId()).orElse(null);
        assertThat((Integer) response.getResult()).isEqualTo(review.getId().intValue());
        assertThat(review.getReviewImage()).isEqualTo("img.com");
    }

    @Test
    @DisplayName("사진과 리뷰 저장시 rating 초과 예외")
    void saveReviewRatingException() throws Exception {
        String url = "http://localhost:8080/reviews";
        String jwt = tokenProvider.createToken(user1.getEmail(), List.of(Role.USER));
        ReviewSaveRequest reviewSaveRequest = new ReviewSaveRequest(6, product1.getId(), "Good!");
        MockMultipartFile json =
                new MockMultipartFile("request", "json", "application/json", objectMapper.writeValueAsString(reviewSaveRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart(url)
                        .file(json)
                        .contentType("multipart/mixed")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("JWT 권한 없는 reviewSave 요청")
    void saveReviewNoAuthException() throws Exception {
        String url = "http://localhost:8080/reviews";
        String jwt = tokenProvider.createToken(user1.getEmail(), List.of(Role.GUEST));
        ReviewSaveRequest reviewSaveRequest = new ReviewSaveRequest(6, product1.getId(), "Good!");
        MockMultipartFile json =
                new MockMultipartFile("request", "json", "application/json", objectMapper.writeValueAsString(reviewSaveRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart(url)
                        .file(json)
                        .contentType("multipart/mixed")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().is4xxClientError());
    }

    void saveMultipartFile() {
        when(fileService.uploadImage(any(MultipartFile.class))).thenReturn("byteImage");
        when(fileService.changeFileNameToUrl(anyString())).thenReturn("img.com");
    }
}
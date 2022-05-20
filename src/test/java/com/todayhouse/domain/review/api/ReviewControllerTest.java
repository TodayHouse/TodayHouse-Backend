package com.todayhouse.domain.review.api;

import com.fasterxml.jackson.core.type.TypeReference;
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
import com.todayhouse.domain.review.domain.Rating;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.dto.request.ReviewSaveRequest;
import com.todayhouse.domain.review.dto.response.ReviewRatingResponse;
import com.todayhouse.domain.review.dto.response.ReviewResponse;
import com.todayhouse.domain.user.dao.SellerRepository;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.global.common.PageDto;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    SellerRepository sellerRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ParentOptionRepository parentOptionRepository;

    @MockBean
    FileService fileService;

    User user1;
    Seller seller1;
    Orders order1;
    Product product1;
    ParentOption option1;

    @BeforeEach
    void setUp() {
        seller1 = sellerRepository.save(Seller.builder().brand("sell1").build());
        user1 = userRepository.save(User.builder().seller(seller1).email("test@test").build());
        product1 = productRepository.save(Product.builder().seller(seller1).build());
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
        Rating rating = new Rating(5, 5, 5, 5, 5);
        ReviewSaveRequest reviewSaveRequest = new ReviewSaveRequest(rating, product1.getId(), "Good!");
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
        Rating rating = new Rating(5, 5, 5, 5, 6);
        ReviewSaveRequest reviewSaveRequest = new ReviewSaveRequest(rating, product1.getId(), "Good!");
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
        Rating rating = new Rating(5, 5, 5, 5, 5);
        ReviewSaveRequest reviewSaveRequest = new ReviewSaveRequest(rating, product1.getId(), "Good!");
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

    @Test
    @DisplayName("image있는 Review 페이징하여 최신순으로 조회")
    void findReviews() throws Exception {
        Rating rating = new Rating(5, 5, 5, 5, 5);
        User user2 = userRepository.save(User.builder().email("user2@test").build());
        User user3 = userRepository.save(User.builder().email("user3@test").build());
        User user4 = userRepository.save(User.builder().email("user4@test").build());
        User user5 = userRepository.save(User.builder().email("user5@test").build());
        Review review2 = reviewRepository.save(Review.builder().user(user2).product(product1).reviewImage("img").rating(rating).build());
        Review review3 = reviewRepository.save(Review.builder().user(user3).product(product1).reviewImage("img").rating(rating).build());
        Review review4 = reviewRepository.save(Review.builder().user(user4).product(product1).rating(rating).build());
        Review review5 = reviewRepository.save(Review.builder().user(user5).product(product1).reviewImage("img").rating(rating).build());
        List<Long> ids = List.of(review5.getId(), review3.getId(), review2.getId());
        String url = "http://localhost:8080/reviews?size=2&page=0&sort=createdAt,DESC&onlyImage=true";
        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        PageDto<ReviewResponse> pageDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getResult()), new TypeReference<>() {
        });
        List<ReviewResponse> reviews = objectMapper.readValue(objectMapper.writeValueAsString(pageDto.getContent()), new TypeReference<>() {
        });

        assertThat(pageDto.getTotalPages()).isEqualTo(2);
        assertThat(pageDto.getTotalElements()).isEqualTo(3);
        for (int i = 0; i < reviews.size(); i++) {
            assertThat(reviews.get(i).getId()).isEqualTo(ids.get(i));
        }
    }

    @Test
    @DisplayName("product1 리뷰의 평점 별 개수 및 평균 조회")
    void findReviewRating() throws Exception {
        User user2 = userRepository.save(User.builder().email("user2@test").build());
        User user3 = userRepository.save(User.builder().email("user3@test").build());
        User user4 = userRepository.save(User.builder().email("user4@test").build());
        User user5 = userRepository.save(User.builder().email("user5@test").build());
        Review review1 = reviewRepository.save(Review.builder().user(user2).product(product1).rating(createRating(5)).build());
        Review review2 = reviewRepository.save(Review.builder().user(user2).product(product1).rating(createRating(5)).build());
        Review review3 = reviewRepository.save(Review.builder().user(user3).product(product1).rating(createRating(4)).build());
        Review review4 = reviewRepository.save(Review.builder().user(user4).product(product1).rating(createRating(3)).build());
        Review review5 = reviewRepository.save(Review.builder().user(user5).product(product1).rating(createRating(3)).build());
        String url = "http://localhost:8080/reviews/ratings/" + product1.getId().intValue();

        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        ReviewRatingResponse reviewRating = objectMapper.convertValue(response.getResult(), ReviewRatingResponse.class);
        assertThat(reviewRating.getOne()).isEqualTo(0);
        assertThat(reviewRating.getTwo()).isEqualTo(0);
        assertThat(reviewRating.getThree()).isEqualTo(2);
        assertThat(reviewRating.getFour()).isEqualTo(1);
        assertThat(reviewRating.getFive()).isEqualTo(2);
        assertThat(reviewRating.getAverage()).isEqualTo(4.0);
    }

    @Test
    @DisplayName("리뷰 작성 가능한지 확인")
    void canReviewWrite() throws Exception {
        String jwt = tokenProvider.createToken(user1.getEmail(), List.of(Role.USER));
        String url = "http://localhost:8080/reviews/writing-validity/" + product1.getId().intValue();
        MvcResult mvcResult = mockMvc.perform(get(url)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        Boolean canWrite = objectMapper.convertValue(response.getResult(), Boolean.class);
        assertTrue(canWrite);
    }

    @Test
    @DisplayName("주문 하지 않은 유저로 리뷰 작성 확인")
    void canReviewWriteNotOrder() throws Exception {
        User user2 = userRepository.save(User.builder().email("test").build());
        String jwt = tokenProvider.createToken(user2.getEmail(), List.of(Role.USER));
        String url = "http://localhost:8080/reviews/writing-validity/" + product1.getId().intValue();
        MvcResult mvcResult = mockMvc.perform(get(url)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andReturn();

        BaseResponse response = getResponseFromMvcResult(mvcResult);
        Boolean fail = objectMapper.convertValue(response.getResult(), Boolean.class);
        assertFalse(fail);
    }

    @Test
    @DisplayName("리뷰 삭제하기")
    void deleteReview() throws Exception {
        reviewRepository.save(Review.builder().user(user1).rating(new Rating(5, 5, 5, 5, 5)).product(product1).build());
        String jwt = tokenProvider.createToken(user1.getEmail(), List.of(Role.USER));
        String url = "http://localhost:8080/reviews/" + product1.getId().intValue();

        mockMvc.perform(delete(url)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());

        Optional<Review> review = reviewRepository.findByUserIdAndProductId(user1.getId(), product1.getId());
        assertTrue(review.isEmpty());
    }

    @Test
    @DisplayName("권한 없는 유저는 리뷰 삭제 불가")
    void deleteReviewNotAuthException() throws Exception {
        String jwt = tokenProvider.createToken(user1.getEmail(), List.of(Role.GUEST));
        String url = "http://localhost:8080/reviews/" + product1.getId().intValue();

        mockMvc.perform(delete(url)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().is4xxClientError())
                .andDo(print());
    }

    private Rating createRating(int totalRating) {
        return new Rating(totalRating, 5, 5, 5, 5);
    }
}
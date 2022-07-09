package com.todayhouse.domain.review.application;

import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.order.dao.OrderRepository;
import com.todayhouse.domain.order.domain.Orders;
import com.todayhouse.domain.order.domain.Status;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.review.dao.ReviewRepository;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.dto.request.ReviewSearchRequest;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReviewServiceImplIntegrityTest extends IntegrationBase {
    @MockBean
    OrderRepository orderRepository;

    @Autowired
    ReviewServiceImpl reviewService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReviewRepository reviewRepository;

    static Product product = null;

    @BeforeAll
    void setUp() {
        Product p1 = Product.builder().title("p1").build();
        User u1 = User.builder().nickname("u1").email("test").build();
        product = productRepository.save(p1);
        userRepository.save(u1);
    }

    @AfterAll
    void cleanUp() {
        reviewRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("리뷰 동시 저장 시 중복 저장되지 않음")
    void reviewSaveConcurrencyTest() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(5);
        final int numberOfThreads = 5;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int j = 0; j < 5; j++) {
            service.execute(() -> {
                try {
                    setSecurityName("test");
                    Review review = Review.builder().rating(5).content("good").build();
                    when(orderRepository.findByUserIdAndProductIdAndStatus(anyLong(), anyLong(), eq(Status.COMPLETED))).thenReturn(List.of(mock(Orders.class)));
                    //테스트 메소드
                    reviewService.saveReview(null, review, product.getId());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                SecurityContextHolder.clearContext();
                latch.countDown();
            });
        }
        latch.await();

        ReviewSearchRequest reviewSearchRequest = new ReviewSearchRequest(null, product.getId(), null, null);
        Page<Review> reviews = reviewService.findReviews(reviewSearchRequest, PageRequest.of(0, 100000));
        long count = reviews.getTotalElements();

        System.out.println("리뷰 수 : " + count);
        assertThat(count).isEqualTo(1L);
    }

    private void setSecurityName(String email) {
        Authentication authentication = mock(Authentication.class);
        // Mockito.whens() for your authorization object
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(securityContext.getAuthentication().getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }
}

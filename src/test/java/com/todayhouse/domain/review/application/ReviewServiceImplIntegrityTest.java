package com.todayhouse.domain.review.application;

import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.review.dao.ReviewRepository;
import com.todayhouse.domain.review.domain.Review;
import com.todayhouse.domain.review.dto.request.ReviewSaveRequest;
import com.todayhouse.domain.review.dto.request.ReviewSearchRequest;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReviewServiceImplIntegrityTest extends IntegrationBase {
    private static List<Long> ids = new ArrayList<>();

    @Autowired
    ReviewServiceImpl reviewService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @BeforeAll
    void setUp() {
        Product p1 = Product.builder().title("p1").build();
        Product p2 = Product.builder().title("p2").build();
        Product p3 = Product.builder().title("p3").build();
        Product p4 = Product.builder().title("p4").build();
        Product p5 = Product.builder().title("p5").build();
        User u1 = User.builder().nickname("u1").email("test").build();
        ids.add(productRepository.save(p1).getId());
        ids.add(productRepository.save(p2).getId());
        ids.add(productRepository.save(p3).getId());
        ids.add(productRepository.save(p4).getId());
        ids.add(productRepository.save(p5).getId());
        userRepository.save(u1);
    }

    @Test
    @DisplayName("리뷰 동시 저장 시 중복되어 저장")
    void reviewSaveConcurrencyTest() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(10);

        for (int j = 0; j < 5; j++) {
            int finalJ = j;
            int numberOfThreads = 2;
            CountDownLatch latch = new CountDownLatch(numberOfThreads);
            for (int i = 0; i < 2; i++) {
                service.execute(() -> {
                    try {
                        setSecurityName("test");
                        ReviewSaveRequest review = new ReviewSaveRequest(5, ids.get(finalJ), "good");
                        //테스트 메소드
                        reviewService.saveReview(null, review);
                        SecurityContextHolder.clearContext();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    latch.countDown();
                });
            }
            latch.await();

            ReviewSearchRequest reviewSearchRequest = new ReviewSearchRequest(null, null, ids.get(finalJ), null);
            Page<Review> reviews = reviewService.findReviews(reviewSearchRequest, PageRequest.of(1, 100));
            long count = reviews.getTotalElements();

            System.out.println("리뷰 수 : " + count); // 리뷰 동시 저장 시 2
            assertThat(count).isEqualTo(1L);
        }
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

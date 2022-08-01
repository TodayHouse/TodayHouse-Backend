package com.todayhouse.domain.likes.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todayhouse.IntegrationBase;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.likes.dao.LikesProductRepository;
import com.todayhouse.domain.likes.domain.LikesProduct;
import com.todayhouse.domain.likes.domain.LikesType;
import com.todayhouse.domain.likes.dto.LikesRequest;
import com.todayhouse.domain.likes.dto.UnLikesRequest;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LikesProductControllerTest extends IntegrationBase {
    @Autowired
    ObjectMapper objectMapper;


    @Autowired
    LikesProductRepository likesProductRepository;

    @Autowired
    ProductRepository productRepository;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenProvider provider;

    User user;
    String jwt;

    String url = "http://localhost:8080/likes";

    Product p1;
    Seller seller1;
    private Product p2;
    @PersistenceContext
    EntityManager em;
    LikesProduct lp;

    @BeforeEach
    void setup() {
        seller1 = Seller.builder().email("seller1@email.com").brand("user1").build();
        user = userRepository.save(User.builder()
                .authProvider(AuthProvider.LOCAL)
                .email("admin@test.com")
                .roles(Collections.singletonList(Role.ADMIN))
                .seller(seller1)
                .nickname("admin1")
                .build());

        jwt = provider.createToken("admin@test.com", Collections.singletonList(Role.USER));

        Category c1 = Category.builder().name("c1").build();
        p1 = Product.builder().seller(seller1).category(c1).build();
        p2 = Product.builder().seller(seller1).category(c1).build();
        em.persist(c1);
        productRepository.save(p1);
        productRepository.save(p2);

        lp = new LikesProduct(user, p1);
        likesProductRepository.save(lp);
        p1.getLikesProducts().add(lp);
    }

    @Test
    @DisplayName("상품좋아요 테스트")
    void productlikes() throws Exception {


        LikesRequest likesRequest = new LikesRequest(LikesType.PRODUCT, p2.getId());
        String jwt = "Bearer " + this.jwt;
        mockMvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(likesRequest))
                        .header("Authorization", jwt)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.likesCount").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("상품조회시 좋아요 확인")
    public void checkProductIsLiked() throws Exception {
        Long id = p1.getId();
        mockMvc.perform(get("http://localhost:8080/products/" + id)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(jsonPath("$.result.liked").value(true))
                .andExpect(jsonPath("$.result.likesCount").value(1));

    }

    @Test
    @DisplayName("게스트 계정으로 조회시 좋아요 체크")
    public void checkProductIsUnliked() throws Exception {
        Long id = p1.getId();
        mockMvc.perform(get("http://localhost:8080/products/" + id)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.liked").value(false))
                .andExpect(jsonPath("$.result.likesCount").value(1));
    }

    @Test
    @DisplayName("상품 좋아요 삭제")
    public void deleteProduct() throws Exception {
        UnLikesRequest unLikesRequest = new UnLikesRequest(LikesType.PRODUCT, p1.getId());

        mockMvc.perform(delete(url)
                        .content(objectMapper.writeValueAsString(unLikesRequest))
                        .header("Authorization", "Bearer " + jwt)
                        .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.liked").value(false))
                .andExpect(jsonPath("$.result.likesCount").value(0));
    }
}

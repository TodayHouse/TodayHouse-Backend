package com.todayhouse.domain.scrap.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.scrap.domain.Scrap;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ScrapRepositoryTest extends DataJpaBase {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ScrapRepository scrapRepository;
    @Autowired
    UserRepository userRepository;

    User user1, user2;
    Product product1, product2;
    Scrap scrap1, scrap2, scrap3;

    @BeforeEach
    void setUp(){
        user1 = userRepository.save(User.builder().build());
        user2 = userRepository.save(User.builder().build());

        product1 = productRepository.save(Product.builder().build());
        product2 = productRepository.save(Product.builder().build());

        scrap1 = scrapRepository.save(Scrap.builder().user(user1).product(product1).build());
        scrap2 = scrapRepository.save(Scrap.builder().user(user1).product(product2).build());
        scrap3 = scrapRepository.save(Scrap.builder().user(user2).product(product1).build());
    }

    @Test
    @DisplayName("유저와 상품으로 스크랩 찾기")
    void findByUserAndProduct() {
        Scrap findScrap1 = scrapRepository.findByUserAndProduct(user1, product1).orElse(null);
        Scrap findScrap2 = scrapRepository.findByUserAndProduct(user1, product2).orElse(null);
        Scrap findScrap3 = scrapRepository.findByUserAndProduct(user2, product1).orElse(null);

        assertThat(findScrap1).isEqualTo(scrap1);
        assertThat(findScrap2).isEqualTo(scrap2);
        assertThat(findScrap3).isEqualTo(scrap3);
    }

    @Test
    @DisplayName("상품 id로 스크랩 개수 세기")
    void countByProductId() {
        Product tmp = productRepository.save(Product.builder().build());

        Long count1 = scrapRepository.countByProduct(product1);
        Long count2 = scrapRepository.countByProduct(product2);
        Long count3 = scrapRepository.countByProduct(tmp);

        assertThat(count1).isEqualTo(2);
        assertThat(count2).isEqualTo(1);
        assertThat(count3).isEqualTo(0);
    }

    @Test
    @DisplayName("유저로 스크랩 개수 세기")
    void countByUser() {
        User tmp = userRepository.save(User.builder().build());

        Long count1 = scrapRepository.countByUser(user1);
        Long count2 = scrapRepository.countByUser(user2);
        Long count3 = scrapRepository.countByUser(tmp);

        assertThat(count1).isEqualTo(2);
        assertThat(count2).isEqualTo(1);
        assertThat(count3).isEqualTo(0);
    }
}
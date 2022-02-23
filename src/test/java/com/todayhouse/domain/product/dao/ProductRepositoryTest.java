package com.todayhouse.domain.product.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.user.dao.SellerRepository;
import com.todayhouse.domain.user.domain.Seller;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductRepositoryTest extends DataJpaBase {

    @Autowired
    CustomProductRepository customProductRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    SellerRepository sellerRepository;

    @BeforeAll
    void preSet() {
        Seller seller = Seller.builder().email("seller@email.com").brandName("house").build();
        sellerRepository.save(seller);
        Product product1 = Product.builder().title("p1").seller(seller).build();
        Product product2 = Product.builder().title("p2").seller(seller).build();
        Product product3 = Product.builder().title("p3").seller(seller).build();
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
    }

    @Test
    void product_페이징() {
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("createdAt").descending());
        Page<Product> page = customProductRepository.findAll(pageRequest);

        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getTotalElements()).isEqualTo(3);
        List<Product> products = page.getContent();
        LocalDateTime time = LocalDateTime.now();
        for (Product p : products) {
            assertThat(time.isAfter(p.getCreatedAt())).isTrue();
            time = p.getCreatedAt();
        }
    }

    @Test
    void product_삭제() {
        productRepository.deleteById(1L);

        List<Product> list = productRepository.findAll();
        assertThat(list.size()).isEqualTo(2);
    }
}
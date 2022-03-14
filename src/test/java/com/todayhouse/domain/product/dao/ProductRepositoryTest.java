package com.todayhouse.domain.product.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.product.domain.ChildOption;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.ProductSearchRequest;
import com.todayhouse.domain.user.dao.SellerRepository;
import com.todayhouse.domain.user.domain.Seller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductRepositoryTest extends DataJpaBase {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    SellerRepository sellerRepository;

    @Autowired
    TestEntityManager em;

    Product product1, product2, product3;

    @BeforeEach
    void preSet() {
        productRepository.deleteAll();

        Seller seller = Seller.builder().email("seller@email.com").brand("house").build();
        em.persist(seller);
        product1 = Product.builder().price(1000).title("p1").seller(seller).build();
        ParentOption op1 = ParentOption.builder().product(product1).content("op1").price(1000).stock(10).build();
        ParentOption op2 = ParentOption.builder().product(product1).content("op2").price(1000).stock(10).build();

        product2 = Product.builder().price(2000).title("p2").seller(seller).build();
        ParentOption op3 = ParentOption.builder().product(product2).content("op3").build();
        ParentOption op4 = ParentOption.builder().product(product2).content("op4").build();
        ChildOption ch1 = ChildOption.builder().parent(op3).content("ch1").stock(10).price(1000).build();
        ChildOption ch2 = ChildOption.builder().parent(op3).content("ch2").stock(20).price(2000).build();
        ChildOption ch3 = ChildOption.builder().parent(op4).content("ch3").stock(30).price(3000).build();
        ChildOption ch4 = ChildOption.builder().parent(op4).content("ch4").stock(40).price(4000).build();

        product3 = Product.builder().price(3000).title("p3").seller(seller).build();
        ParentOption op5 = ParentOption.builder().product(product3).content("op5").price(5555).stock(0).build();

        em.persist(product1);
        em.persist(product2);
        em.persist(product3);

        em.flush();
        em.clear();
    }

    @Test
    void 가격_2000_이상_product_페이징() {
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("createdAt").descending());
        ProductSearchRequest productSearch = ProductSearchRequest.builder().priceFrom(2000).build();
        Page<Product> page = productRepository.findAllWithSeller(productSearch, pageRequest);

        assertThat(page.getTotalPages()).isEqualTo(1);
        assertThat(page.getTotalElements()).isEqualTo(2);
        List<Product> products = page.getContent();
        LocalDateTime time = LocalDateTime.now();
        for (Product p : products) {
            assertThat(time.isAfter(p.getCreatedAt())).isTrue();
            time = p.getCreatedAt();
        }
    }

    @Test
    void product_삭제() {
        productRepository.deleteById(product1.getId());
        System.out.println(product1.getId());
        List<Product> list = productRepository.findAll();
        for (Product p : list) {
            System.out.println(p.toString());
        }
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    void product_하나_찾기() {
        Product product = productRepository.findByIdWithOptionsAndSellerAndImages(product2.getId()).orElse(null);

        assertThat(product.getSeller().getBrand()).isEqualTo(product.getBrand());
        assertThat(product.getTitle()).isEqualTo("p2");
        assertThat(product.getOptions().size()).isEqualTo(2);
        assertTrue(product.getOptions().stream().allMatch(op -> op.getChildren().size() == 2)); //childOption 모두 2개
    }
}
package com.todayhouse.domain.image.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.image.domain.ProductImage;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.user.domain.Seller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ProductImageRepositoryTest extends DataJpaBase {

    @Autowired
    TestEntityManager em;

    @Autowired
    ProductImageRepository productImageRepository;

    Product product;
    ProductImage img1, img2;

    @BeforeEach
    void setUp(){
        Seller seller = Seller.builder().brand("testBrand").build();
        product = Product.builder().image("img.jpg").title("test").seller(seller).build();
        img1 = ProductImage.builder().fileName("img1.jpg").product(product).build();
        img2 = ProductImage.builder().fileName("img2.jpg").product(product).build();
        em.persist(seller);
        em.persist(product);
        em.persist(img1);
        em.persist(img2);
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("product의 처음 img 찾기")
    void findFirstByProductOrderByCreatedAtDesc() {
        ProductImage productImage = productImageRepository.findFirstByProductOrderByCreatedAtAsc(product).orElse(null);

        assertThat(productImage.getId()).isEqualTo(img1.getId());
    }

    @Test
    @DisplayName("product의 image 개수")
    void countById() {
        Long count = productImageRepository.countByProductId(product.getId());

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("product image 삭제")
    void deleteByFileName() {
        productImageRepository.deleteByFileName("img2.jpg");

        assertThat(productImageRepository.findById(img2.getId())).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("product images 찾기")
    void findByProductId() {
        List<ProductImage> images = productImageRepository.findByProductId(product.getId());

        assertThat(images.size()).isEqualTo(2);
        assertThat(images.stream().anyMatch(i->i.getFileName().equals("img1.jpg")));
        assertThat(images.stream().anyMatch(i->i.getFileName().equals("img2.jpg")));
    }

    @Test
    @DisplayName("product images 찾기")
    void findByProductIdWithOptions() {
        List<ProductImage> images = productImageRepository.findByProductId(product.getId());

        assertThat(images.stream().anyMatch(i->i.getFileName().equals("img1.jpg")));
        assertThat(images.stream().anyMatch(i->i.getFileName().equals("img2.jpg")));
    }


}
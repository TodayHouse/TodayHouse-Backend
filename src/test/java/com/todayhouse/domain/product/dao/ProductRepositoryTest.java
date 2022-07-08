package com.todayhouse.domain.product.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.category.dao.CategoryRepository;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.image.dao.ProductImageRepository;
import com.todayhouse.domain.image.domain.ProductImage;
import com.todayhouse.domain.product.domain.ChildOption;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.dto.request.ProductSearchRequest;
import com.todayhouse.domain.user.dao.SellerRepository;
import com.todayhouse.domain.user.domain.Seller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductRepositoryTest extends DataJpaBase {

    @Autowired
    SellerRepository sellerRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductImageRepository productImageRepository;

    Product product1, product2, product3;
    Category c1, c2, c3;
    Seller seller;

    @BeforeEach
    void preSet() {
        productRepository.deleteAllInBatch();
        c1 = Category.builder().name("c1").build();
        c2 = Category.builder().parent(c1).name("c2").build();
        c3 = Category.builder().parent(c2).name("c3").build();
        categoryRepository.save(c1);

        seller = Seller.builder().email("seller@email.com").brand("house").build();
        sellerRepository.save(seller);
        product1 = Product.builder().category(c1).price(1000).title("p1").seller(seller).productDetail("abcd").build();
        ParentOption op1 = ParentOption.builder().product(product1).content("op1").price(1000).stock(10).build();
        ParentOption op2 = ParentOption.builder().product(product1).content("op2").price(1000).stock(10).build();

        product2 = Product.builder().category(c2).price(2000).title("p2").seller(seller).productDetail("bcde").build();
        ParentOption op3 = ParentOption.builder().product(product2).content("op3").build();
        ParentOption op4 = ParentOption.builder().product(product2).content("op4").build();
        ChildOption ch1 = ChildOption.builder().parent(op3).content("ch1").stock(10).price(1000).build();
        ChildOption ch2 = ChildOption.builder().parent(op3).content("ch2").stock(20).price(2000).build();
        ChildOption ch3 = ChildOption.builder().parent(op4).content("ch3").stock(30).price(3000).build();
        ChildOption ch4 = ChildOption.builder().parent(op4).content("ch4").stock(40).price(4000).build();
        ProductImage file1 = ProductImage.builder().fileName("file1").product(product2).build();
        ProductImage file2 = ProductImage.builder().fileName("file2").product(product2).build();

        product3 = Product.builder().category(c3).price(3000).title("p3").seller(seller).productDetail("cdef").build();
        ParentOption op5 = ParentOption.builder().product(product3).content("op5").price(5555).stock(0).build();

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
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
            assertTrue(time.isAfter(p.getCreatedAt()));
            time = p.getCreatedAt();
        }
    }

    @Test
    void product_삭제() {
        productRepository.deleteById(product1.getId());
        List<Product> list = productRepository.findAll();

        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    void product_하나_찾기() {
        Product product = productRepository.findByIdWithOptionsAndSeller(product2.getId()).orElse(null);

        assertThat(product.getCategory().getId()).isEqualTo(c2.getId());
        assertThat(product.getSeller().getBrand()).isEqualTo(product.getBrand());
        assertThat(product.getTitle()).isEqualTo("p2");
        assertThat(product.getParents().size()).isEqualTo(2);
        assertTrue(product.getParents().stream().allMatch(op -> op.getChildren().size() == 2)); //childOption 모두 2개
    }

    @Test
    void product_조건으로_찾기() {
        ProductSearchRequest request = ProductSearchRequest.builder()
                .categoryName(c1.getName()).priceFrom(2000).priceTo(3000).brand("house")
                .build();
        PageRequest of = PageRequest.of(0, 30, Sort.by("createdAt").descending());
        Page<Product> page = productRepository.findAllWithSeller(request, of);
        List<Product> list = page.getContent();


        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0).getId()).isEqualTo(product3.getId());
        assertThat(list.get(1).getId()).isEqualTo(product2.getId());
    }

    @Test
    @DisplayName("존재하지 않는 category name으로 찾기")
    void findAllWithSeller() {
        ProductSearchRequest request = ProductSearchRequest.builder()
                .categoryName("fail").build();
        PageRequest of = PageRequest.of(0, 30, Sort.by("createdAt").descending());
        Page<Product> page = productRepository.findAllWithSeller(request, of);
        List<Product> list = page.getContent();

        assertThat(list.size()).isZero();
    }

    @Test
    @DisplayName("deliveryFee와 specialPrice가 false로 id순 페이징")
    void findAllWithSellerFalse() {
        ProductSearchRequest request = ProductSearchRequest.builder()
                .deliveryFee(false).specialPrice(false).build();
        PageRequest of = PageRequest.of(0, 30, Sort.by("id"));
        Page<Product> page = productRepository.findAllWithSeller(request, of);
        List<Product> list = page.getContent();

        assertThat(list.size()).isEqualTo(3);
        assertThat(list.get(0).getId()).isEqualTo(product1.getId());
        assertThat(list.get(1).getId()).isEqualTo(product2.getId());
        assertThat(list.get(2).getId()).isEqualTo(product3.getId());
    }

    @Test
    @DisplayName("브랜드 이름으로 검색")
    void findAllWithSellerSearchBrand(){
        String search = "hou";
        ProductSearchRequest request = ProductSearchRequest.builder().search(search).build();
        PageRequest pageRequest = PageRequest.of(0, 30, Sort.by("id"));

        Page<Product> page = productRepository.findAllWithSeller(request, pageRequest);

        assertThat(page.getContent().size()).isEqualTo(3);
        assertThat(page.getContent().get(0)).isEqualTo(product1);
        assertThat(page.getContent().get(1)).isEqualTo(product2);
        assertThat(page.getContent().get(2)).isEqualTo(product3);
    }

    @Test
    @DisplayName("상품 제목으로 검색")
    void findAllWithSellerSearchTitle() {
        String search = "p1";
        ProductSearchRequest request = ProductSearchRequest.builder().search(search).build();
        PageRequest pageRequest = PageRequest.of(0, 30, Sort.by("id"));

        Page<Product> page = productRepository.findAllWithSeller(request, pageRequest);

        assertThat(page.getContent().size()).isEqualTo(1);
        assertThat(page.getContent().get(0)).isEqualTo(product1);
    }
}
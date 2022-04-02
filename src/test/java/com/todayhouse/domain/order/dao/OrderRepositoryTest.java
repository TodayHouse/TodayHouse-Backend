package com.todayhouse.domain.order.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.category.dao.CategoryRepository;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.order.domain.Order;
import com.todayhouse.domain.product.dao.ParentOptionRepository;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.ChildOption;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.domain.SelectionOption;
import com.todayhouse.domain.user.dao.SellerRepository;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class OrderRepositoryTest extends DataJpaBase {

    @Autowired
    TestEntityManager em;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    SellerRepository sellerRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ParentOptionRepository parentOptionRepository;

    Product product1, product2;
    ParentOption op1, op2, op3, op4;
    ChildOption ch1, ch2;
    SelectionOption s1;

    @BeforeEach
    void setUp() {
        Category c1 = Category.builder().name("c1").build();
        categoryRepository.save(c1);

        Seller seller = Seller.builder().email("seller@email.com").brand("house").build();
        sellerRepository.save(seller);

        product1 = Product.builder().category(c1).price(1000).title("p1").seller(seller).build();
        op1 = ParentOption.builder().product(product1).content("op1").price(1000).stock(10).build();
        op2 = ParentOption.builder().product(product1).content("op2").price(1000).stock(10).build();
        s1 = SelectionOption.builder().product(product1).content("s1").price(500).stock(1).build();

        product2 = Product.builder().category(c1).price(2000).title("p2").seller(seller).build();
        op3 = ParentOption.builder().product(product2).content("op3").build();
        op4 = ParentOption.builder().product(product2).content("op4").build();
        ch1 = ChildOption.builder().parent(op3).content("ch1").stock(10).price(1000).build();
        ch2 = ChildOption.builder().parent(op3).content("ch2").stock(20).price(2000).build();

        productRepository.save(product1);
        productRepository.save(product2);
    }

    @Test
    @DisplayName("user id로 주문 내림차순 찾기")
    void findByUserIdOrderByCreatedAtDesc() {
        User user = User.builder().build();
        em.persist(user);

        Order order1 = Order.builder().user(user).product(product1).build();
        Order order2 = Order.builder().user(user).product(product1).build();
        Order order3 = Order.builder().user(user).product(product1).build();

        em.persist(order1);
        em.persist(order2);
        em.persist(order3);
        em.clear();
        em.flush();

        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        assertThat(orders.size()).isEqualTo(3);
        assertThat(orders.get(0).getId()).isEqualTo(order3.getId());
        assertThat(orders.get(2).getId()).isEqualTo(order1.getId());
    }

    @Test
    @DisplayName("OrderId로 option과 fetch join한 order 찾기")
    void findByIdWithOptions() {
        Order order = Order.builder().product(product1).parentOption(op1).selectionOption(s1).build();
        em.persist(order);
        em.flush();
        em.clear();

        Order find = orderRepository.findByIdWithOptions(order.getId()).orElse(null);
        assertThat(find.getParentOption().getId()).isEqualTo(op1.getId());
        assertThat(find.getSelectionOption().getId()).isEqualTo(s1.getId());
    }
}
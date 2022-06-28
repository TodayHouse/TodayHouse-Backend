package com.todayhouse.domain.order.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.category.dao.CategoryRepository;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.order.domain.Orders;
import com.todayhouse.domain.order.domain.Status;
import com.todayhouse.domain.product.dao.ParentOptionRepository;
import com.todayhouse.domain.product.dao.ProductRepository;
import com.todayhouse.domain.product.domain.ChildOption;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.domain.SelectionOption;
import com.todayhouse.domain.user.dao.SellerRepository;
import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class OrdersRepositoryTest extends DataJpaBase {
    @Autowired
    UserRepository userRepository;

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
        ch1 = ChildOption.builder().parent(op3).content("ch1").stock(100).price(1000).build();
        ch2 = ChildOption.builder().parent(op3).content("ch2").stock(200).price(2000).build();

        productRepository.save(product1);
        productRepository.save(product2);
    }

    @Test
    @DisplayName("totalPrice 내림차순 주문 조회")
    void findByUserIdWithProduct() {
        User user = User.builder().build();
        userRepository.save(user);

        Orders orders1 = Orders.builder().user(user).product(product1)
                .parentOption(op1).productQuantity(1)
                .selectionOption(s1).selectionQuantity(1).build(); //1500
        Orders orders2 = Orders.builder().user(user).product(product2)
                .parentOption(op3).childOption(ch1).productQuantity(100).build(); //100000
        Orders orders3 = Orders.builder().user(user).product(product1)
                .parentOption(op1).productQuantity(10).build(); //10000

        orderRepository.save(orders1);
        orderRepository.save(orders2);
        orderRepository.save(orders3);

        PageRequest request = PageRequest.of(0, 10, Sort.by("totalPrice").descending());

        Page<Orders> orders = orderRepository.findAllByUserIdWithProductAndOptions(user.getId(), request);
        assertThat(orders.getContent().size()).isEqualTo(3);
        assertThat(orders.getContent().get(0).getId()).isEqualTo(orders2.getId());
        assertThat(orders.getContent().get(0).getProduct().getId()).isEqualTo(product2.getId());
        assertThat(orders.getContent().get(2).getId()).isEqualTo(orders1.getId());
        assertThat(orders.getContent().get(2).getProduct().getId()).isEqualTo(product1.getId());
    }

    @Test
    @DisplayName("OrderId로 product, option과 fetch join한 order 찾기")
    void findByIdWithProductAndOptions() {
        Orders orders = Orders.builder().product(product1).parentOption(op1).selectionOption(s1).build();
        orderRepository.save(orders);

        Orders find = orderRepository.findByIdWithProductAndOptions(orders.getId()).orElse(null);
        assertThat(find.getProduct().getId()).isEqualTo(product1.getId());
        assertThat(find.getParentOption().getId()).isEqualTo(op1.getId());
        assertThat(find.getSelectionOption().getId()).isEqualTo(s1.getId());
    }

    @Test
    @DisplayName("order를 userId, productId, status로 찾기")
    void findByUserIdAndProductIdAndStatus() {
        User user = User.builder().email("test").build();
        userRepository.save(user);
        Orders order1 = Orders.builder().product(product1).parentOption(op1).user(user).build();
        Orders order2 = Orders.builder().product(product1).parentOption(op1).user(user).build();
        order1.updateStatus(Status.COMPLETED);
        order2.updateStatus(Status.COMPLETED);
        orderRepository.save(order1);
        orderRepository.save(order2);

        List<Orders> orders = orderRepository.findByUserIdAndProductIdAndStatus(user.getId(), product1.getId(), Status.COMPLETED);

        assertThat(orders.get(0).getId()).isEqualTo(order1.getId());
        assertThat(orders.get(1).getId()).isEqualTo(order2.getId());
    }

    @Test
    @DisplayName("존재하지 않는 userId로 조회")
    void findAllByUserIdWithProductAndOptions() {
        PageRequest request = PageRequest.of(0, 10);

        Page<Orders> page = orderRepository.findAllByUserIdWithProductAndOptions(10000L, request);

        assertThat(page.getTotalElements()).isZero();
        assertThat(page.getContent().size()).isZero();

    }
}
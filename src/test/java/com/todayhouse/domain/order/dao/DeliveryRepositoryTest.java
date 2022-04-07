package com.todayhouse.domain.order.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.order.domain.Delivery;
import com.todayhouse.domain.order.domain.Order;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.user.domain.Seller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DeliveryRepositoryTest extends DataJpaBase {

    @Autowired
    TestEntityManager em;

    @Autowired
    DeliveryRepository deliveryRepository;

    @Test
    @DisplayName("orderId로 order, delivery 찾기")
    void findByOrderIdWithOrder() {
        Seller seller = Seller.builder().brand("test").build();
        Product product = Product.builder().seller(seller).build();
        ParentOption op = ParentOption.builder().product(product).build();
        Order order = Order.builder().parentOption(op).productQuantity(1).build();
        Delivery delivery = Delivery.builder().order(order).build();
        em.persist(seller);
        em.persist(op);
        em.persist(delivery);
        em.flush();
        em.clear();

        Delivery find = deliveryRepository.findByOrderIdWithOrder(delivery.getOrder().getId()).orElse(null);
        assertThat(find.getId()).isEqualTo(delivery.getId());
    }
}
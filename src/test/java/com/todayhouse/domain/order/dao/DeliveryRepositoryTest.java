package com.todayhouse.domain.order.dao;

import com.todayhouse.DataJpaBase;
import com.todayhouse.domain.order.domain.Delivery;
import com.todayhouse.domain.order.domain.Orders;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.user.domain.Seller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
        Orders orders = Orders.builder().parentOption(op).productQuantity(1).build();
        Delivery delivery = Delivery.builder().order(orders).build();
        em.persist(seller);
        em.persist(op);
        em.persist(delivery);
        em.flush();
        em.clear();

        Delivery find = deliveryRepository.findByOrderIdWithOrder(delivery.getOrder().getId()).orElse(null);
        assertThat(find.getId()).isEqualTo(delivery.getId());
    }
}
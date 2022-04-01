package com.todayhouse.domain.order.dao;

import com.todayhouse.domain.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("select o from Order o left join fetch o.parentOption left join fetch o.childOption " +
            "left join fetch o.selectionOption where o.id=:orderId")
    Order findByIdWithOptions(@Param("orderId") Long orderId);
}

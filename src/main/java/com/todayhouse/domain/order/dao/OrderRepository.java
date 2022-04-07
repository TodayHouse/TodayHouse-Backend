package com.todayhouse.domain.order.dao;

import com.todayhouse.domain.order.domain.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long>, CustomOrderRepository{
    Page<Orders> findByUserIdWithProduct(Long userId, Pageable pageable);

    @Query("select o from Orders o left join fetch o.parentOption left join fetch o.childOption " +
            "left join fetch o.selectionOption where o.id=:orderId")
    Optional<Orders> findByIdWithOptions(@Param("orderId") Long orderId);
}

package com.todayhouse.domain.order.dao;

import com.todayhouse.domain.order.domain.Orders;
import com.todayhouse.domain.order.domain.Status;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long>, CustomOrderRepository {
    Page<Orders> findAllByUserIdWithProductAndOptions(Long userId, Pageable pageable);

    @Query("select o from Orders o join fetch o.product " +
            "left join fetch o.parentOption " +
            "left join fetch o.childOption " +
            "left join fetch o.selectionOption where o.id=:orderId")
    Optional<Orders> findByIdWithProductAndOptions(@Param("orderId") Long orderId);

    Optional<Orders> findFirstByUserAndProductOrderByIdDesc(User user, Product product);

    List<Orders> findByUserIdAndProductIdAndStatus(Long userId, Long productId, Status status);
}

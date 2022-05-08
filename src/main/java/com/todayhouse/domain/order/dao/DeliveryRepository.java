package com.todayhouse.domain.order.dao;

import com.todayhouse.domain.order.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    @Query("select d from Delivery d join fetch d.order where d.order.id=:orderId")
    Optional<Delivery> findByOrderIdWithOrder(@Param("orderId") Long orderId);
}

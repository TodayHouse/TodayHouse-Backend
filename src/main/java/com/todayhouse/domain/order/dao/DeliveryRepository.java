package com.todayhouse.domain.order.dao;

import com.todayhouse.domain.order.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}

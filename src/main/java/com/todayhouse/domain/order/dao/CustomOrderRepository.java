package com.todayhouse.domain.order.dao;

import com.todayhouse.domain.order.domain.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomOrderRepository {
    Page<Orders> findAllByUserIdWithProductAndOptions(Long userId, Pageable pageable);
}

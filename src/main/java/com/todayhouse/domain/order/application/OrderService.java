package com.todayhouse.domain.order.application;

import com.todayhouse.domain.order.domain.Orders;
import com.todayhouse.domain.order.dto.request.DeliverySaveRequest;
import com.todayhouse.domain.order.dto.request.OrderSaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Orders saveOrder(OrderSaveRequest request);

    Page<Orders> findOrders(Pageable pageable);

    void cancelOrder(Long orderId);

    void completeOrder(Long orderId);
}

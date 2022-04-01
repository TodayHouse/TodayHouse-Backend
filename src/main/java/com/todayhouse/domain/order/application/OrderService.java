package com.todayhouse.domain.order.application;

import com.todayhouse.domain.order.domain.Order;
import com.todayhouse.domain.order.dto.request.OrderSaveRequest;

import java.util.List;

public interface OrderService {
    Order saveOrder(OrderSaveRequest request);
    List<Order> findByUserName(String userName);
    void cancelOrder(Long orderId);
    void completeOrder(Long orderId);
}

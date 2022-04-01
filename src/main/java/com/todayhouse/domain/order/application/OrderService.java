package com.todayhouse.domain.order.application;

import com.todayhouse.domain.order.domain.Order;
import com.todayhouse.domain.order.dto.request.DeliverySaveRequest;
import com.todayhouse.domain.order.dto.request.OrderSaveRequest;

import java.util.List;

public interface OrderService {
    Order saveOrder(OrderSaveRequest orderRequest, DeliverySaveRequest deliveryRequest);

    List<Order> findOrders();

    void cancelOrder(Long orderId);

    void completeOrder(Long orderId);
}

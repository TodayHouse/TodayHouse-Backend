package com.todayhouse.domain.order.application;

import com.todayhouse.domain.order.domain.Orders;
import com.todayhouse.domain.order.dto.request.OrderSaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    List<Orders> saveOrders(List<OrderSaveRequest> requests);

    Page<Orders> findOrders(Pageable pageable);

    Long findMyOrderIdByProductId(Long productId);

    void cancelOrder(Long orderId);

    void completeOrder(Long orderId);
}

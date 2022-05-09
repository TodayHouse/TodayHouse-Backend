package com.todayhouse.domain.order.application;

import com.todayhouse.domain.order.domain.Delivery;

public interface DeliveryService {
    Delivery findDeliveryByOrderIdWithOrder(Long orderId);
}

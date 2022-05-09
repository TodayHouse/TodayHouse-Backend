package com.todayhouse.domain.order.application;

import com.todayhouse.domain.order.dao.DeliveryRepository;
import com.todayhouse.domain.order.dao.OrderRepository;
import com.todayhouse.domain.order.domain.Delivery;
import com.todayhouse.domain.order.exception.DeliveryNotFoundException;
import com.todayhouse.domain.order.exception.OrderNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;

    @Override
    @Transactional(readOnly = true)
    public Delivery findDeliveryByOrderIdWithOrder(Long orderId) {
        orderRepository.findByIdWithProductAndOptions(orderId).orElseThrow(OrderNotFoundException::new);
        Delivery delivery = deliveryRepository.findByOrderIdWithOrder(orderId).orElseThrow(DeliveryNotFoundException::new);
        return delivery;
    }
}

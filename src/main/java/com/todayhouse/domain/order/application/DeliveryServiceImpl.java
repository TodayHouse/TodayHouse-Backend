package com.todayhouse.domain.order.application;

import com.todayhouse.domain.order.dao.DeliveryRepository;
import com.todayhouse.domain.order.domain.Delivery;
import com.todayhouse.domain.order.exception.DeliveryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;

    @Override
    @Transactional(readOnly = true)
    public Delivery findDeliveryByOrderIdWithOrder(Long orderId) {
        return deliveryRepository.findByOrderIdWithOrder(orderId).orElseThrow(DeliveryNotFoundException::new);
    }
}

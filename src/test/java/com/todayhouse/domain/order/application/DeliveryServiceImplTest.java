package com.todayhouse.domain.order.application;

import com.todayhouse.domain.order.dao.DeliveryRepository;
import com.todayhouse.domain.order.domain.Delivery;
import com.todayhouse.domain.order.exception.DeliveryNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {
    @InjectMocks
    DeliveryServiceImpl deliveryService;

    @Mock
    DeliveryRepository deliveryRepository;

    @Test
    @DisplayName("orderId로 delivery 찾기")
    void findDeliveryByOrderIdWithOrder() {
        Delivery mock = Mockito.mock(Delivery.class);

        when(deliveryRepository.findByOrderIdWithOrder(anyLong())).thenReturn(Optional.ofNullable(mock));

        assertThat(deliveryService.findDeliveryByOrderIdWithOrder(1L)).isEqualTo(mock);
    }

    @Test
    @DisplayName("orderId로 delivery 찾을 수 없음")
    void findDeliveryByOrderIdWithOrderException() {
        when(deliveryRepository.findByOrderIdWithOrder(anyLong())).thenReturn(Optional.ofNullable(null));

        assertThrows(DeliveryNotFoundException.class, () -> deliveryService.findDeliveryByOrderIdWithOrder(1L));
    }
}
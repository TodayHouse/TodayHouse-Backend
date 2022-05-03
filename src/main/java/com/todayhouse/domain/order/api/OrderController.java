package com.todayhouse.domain.order.api;

import com.todayhouse.domain.order.application.DeliveryService;
import com.todayhouse.domain.order.application.OrderService;
import com.todayhouse.domain.order.domain.Delivery;
import com.todayhouse.domain.order.domain.Orders;
import com.todayhouse.domain.order.dto.request.OrderSaveRequest;
import com.todayhouse.domain.order.dto.response.OrderResponse;
import com.todayhouse.global.common.BaseResponse;
import com.todayhouse.global.common.PageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final DeliveryService deliveryService;

    @PostMapping
    public BaseResponse<List<Long>> saveOrders(@RequestBody List<OrderSaveRequest> orderRequests) {
        List<Orders> orders = orderService.saveOrders(orderRequests);
        List<Long> ids = orders.stream().map(o -> o.getId()).collect(Collectors.toList());
        return new BaseResponse(ids);
    }

    /*
    ?page=0&size=4&sort=createdAt,DESC&sort=id,DESC 형식으로 작성
    jwt를 이용해 유저검색
    */
    @GetMapping
    public BaseResponse<Page<OrderResponse>> findUserOrdersPaging(Pageable pageable) {
        Page<Orders> orders = orderService.findOrders(pageable);
        PageDto<OrderResponse> response = new PageDto<>(orders.map(order -> new OrderResponse(order)));
        return new BaseResponse(response);
    }

    @GetMapping("/{id}")
    public BaseResponse<OrderResponse> findOrderDetail(@PathVariable Long id) {
        Delivery delivery = deliveryService.findDeliveryByOrderIdWithOrder(id);
        OrderResponse response = new OrderResponse(delivery);
        return new BaseResponse(response);
    }

    @PutMapping("/cancel/{id}")
    public BaseResponse cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return new BaseResponse("취소되었습니다.");
    }

    @PutMapping("/complete/{id}")
    public BaseResponse completeOrder(@PathVariable Long id) {
        orderService.completeOrder(id);
        return new BaseResponse("완료되었습니다.");
    }
}

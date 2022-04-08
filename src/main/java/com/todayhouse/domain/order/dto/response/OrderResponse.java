package com.todayhouse.domain.order.dto.response;

import com.todayhouse.domain.order.domain.Delivery;
import com.todayhouse.domain.order.domain.Orders;
import com.todayhouse.domain.order.domain.Status;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResponse {
    private Long id;
    private String memo;
    private String brand;
    private String title;
    private String imageUrl;
    private String childOption;
    private String parentOption;
    private String selectionOptions;
    private Status status;
    private Integer totalPrice;
    private Integer productQuantity;
    private Integer selectionQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private DeliveryResponse deliveryResponse;

    public OrderResponse(Orders orders) {
        setOrder(orders);
    }

    public OrderResponse(Delivery delivery) {
        setOrder(delivery.getOrder());
        this.deliveryResponse = new DeliveryResponse(delivery);
    }

    private void setOrder(Orders orders) {
        this.id = orders.getId();
        this.memo = orders.getMemo();
        this.brand = orders.getProduct().getBrand();
        this.title = orders.getProduct().getTitle();
        this.status = orders.getStatus();
        this.totalPrice = orders.getTotalPrice();
        this.imageUrl = orders.getProduct().getImage();
        this.createdAt = orders.getCreatedAt();
        this.updatedAt = orders.getUpdatedAt();
        this.childOption = orders.getChildOption() == null ? null : orders.getChildOption().getContent();
        this.parentOption = orders.getParentOption().getContent();
        this.selectionOptions = orders.getSelectionOption() == null ? null : orders.getSelectionOption().getContent();
        this.productQuantity = orders.getProductQuantity();
        this.selectionQuantity = orders.getSelectionQuantity();
    }
}

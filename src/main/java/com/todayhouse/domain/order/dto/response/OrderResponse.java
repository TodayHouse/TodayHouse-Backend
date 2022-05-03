package com.todayhouse.domain.order.dto.response;

import com.todayhouse.domain.order.domain.Delivery;
import com.todayhouse.domain.order.domain.Orders;
import com.todayhouse.domain.order.domain.Status;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.domain.SelectionOption;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResponse {
    private Long id;
    private String memo;
    private Status status;
    private Integer deliveryFee;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> productInfo;
    private List<String> selectionOptionInfo;
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
        this.status = orders.getStatus();
        this.deliveryFee = orders.getDeliveryFee();
        this.createdAt = orders.getCreatedAt();
        this.updatedAt = orders.getUpdatedAt();
        this.productInfo = createProductInfo(orders);
        if (orders.getSelectionOption() != null)
            this.selectionOptionInfo = createSelectionOptionInfo(orders.getSelectionOption(), orders.getSelectionQuantity());
    }

    private List<String> createProductInfo(Orders orders) {
        Product product = orders.getProduct();
        String optionName = orders.getParentOption().getContent();
        if (orders.getChildOption() != null) optionName += " / " + orders.getChildOption().getContent();
        return List.of(product.getImage(), product.getTitle(), product.getBrand(), optionName, Integer.toString(orders.getTotalPrice()), Integer.toString(orders.getProductQuantity()));
    }

    private List<String> createSelectionOptionInfo(SelectionOption selectionOption, int selectionQuantity) {
        return List.of(selectionOption.getContent(), Integer.toString(selectionQuantity));
    }
}

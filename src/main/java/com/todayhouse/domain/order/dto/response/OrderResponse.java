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

    public OrderResponse(Orders orders, String imageUrl) {
        this.id = orders.getId();
        this.memo = orders.getMemo();
        this.status = orders.getStatus();
        this.createdAt = orders.getCreatedAt();
        this.updatedAt = orders.getUpdatedAt();
        this.deliveryFee = orders.getProduct().getDeliveryFee();
        this.productInfo = createProductInfo(orders, imageUrl);
        if (orders.getSelectionOption() != null)
            this.selectionOptionInfo = createSelectionOptionInfo(orders.getSelectionOption(), orders.getSelectionQuantity());
    }

    public OrderResponse(Delivery delivery, String imageUrl) {
        this(delivery.getOrder(), imageUrl);
        this.deliveryResponse = new DeliveryResponse(delivery);
    }

    private List<String> createProductInfo(Orders orders, String imageUrl) {
        Product product = orders.getProduct();
        String optionName = orders.getParentOption().getContent();
        if (orders.getChildOption() != null) optionName += " / " + orders.getChildOption().getContent();
        return List.of(imageUrl, product.getTitle(), product.getBrand(), optionName, Integer.toString(orders.getTotalPrice()), Integer.toString(orders.getProductQuantity()));
    }

    private List<String> createSelectionOptionInfo(SelectionOption selectionOption, int selectionQuantity) {
        return List.of(selectionOption.getContent(), Integer.toString(selectionQuantity));
    }
}

package com.todayhouse.domain.order.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.todayhouse.domain.order.domain.Orders;
import com.todayhouse.domain.order.domain.Status;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderResponse {
    private Long id;
    private String memo;
    private String imageUrl;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String childOption;
    private String parentOption;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String selectionOptions;
    private Status status;
    private Integer price;
    private Integer productQuantity;
    private Integer selectionQuantity;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;

    public OrderResponse(Orders orders){
        this.id = orders.getId();
        this.memo = orders.getMemo();
        this.status = orders.getStatus();
        this.price = orders.getTotalPrice();
        this.imageUrl = orders.getProduct().getImage();
        this.createAt = orders.getCreatedAt();
        this.updatedAt = orders.getUpdatedAt();
        this.childOption = orders.getChildOption() == null ? null : orders.getChildOption().getContent();
        this.parentOption = orders.getParentOption().getContent();
        this.selectionOptions = orders.getSelectionOption() == null ? null : orders.getSelectionOption().getContent();
        this.productQuantity = orders.getProductQuantity();
        this.selectionQuantity = orders.getSelectionQuantity();
    }

}

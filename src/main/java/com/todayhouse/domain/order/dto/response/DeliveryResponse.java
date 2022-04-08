package com.todayhouse.domain.order.dto.response;

import com.todayhouse.domain.order.domain.Address;
import com.todayhouse.domain.order.domain.Delivery;
import lombok.Getter;

import javax.persistence.Embedded;

@Getter
public class DeliveryResponse {
    private String sender;
    private String receiver;
    private String senderPhoneNumber;
    private String receiverPhoneNumber;
    @Embedded
    private Address address;
    private OrderResponse order;

    public DeliveryResponse (Delivery delivery){
        this.order = new OrderResponse(delivery.getOrder());
        this.sender = delivery.getSender();
        this.receiver = delivery.getReceiver();
        this.address = delivery.getAddress();
        this.senderPhoneNumber = delivery.getSenderPhoneNumber();
        this.receiverPhoneNumber = delivery.getReceiverPhoneNumber();
    }
}

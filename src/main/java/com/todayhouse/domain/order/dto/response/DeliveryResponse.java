package com.todayhouse.domain.order.dto.response;

import com.todayhouse.domain.order.domain.Address;
import com.todayhouse.domain.order.domain.Delivery;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryResponse {
    private String sender;
    private String receiver;
    private String senderPhoneNumber;
    private String receiverPhoneNumber;
    private Address address;

    public DeliveryResponse(Delivery delivery) {
        this.sender = delivery.getSender();
        this.receiver = delivery.getReceiver();
        this.address = delivery.getAddress();
        this.senderPhoneNumber = delivery.getSenderPhoneNumber();
        this.receiverPhoneNumber = delivery.getReceiverPhoneNumber();
    }
}

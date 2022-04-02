package com.todayhouse.domain.order.dto.request;

import com.todayhouse.domain.order.domain.Address;
import com.todayhouse.domain.order.domain.Delivery;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DeliverySaveRequest {
    @NotBlank(message = "sender를 입력해주세요.")
    String sender;
    @NotBlank(message = "receiver를 입력해주세요.")
    String receiver;
    @NotBlank(message = "senderPhoneNumber를 입력해주세요.")
    String senderPhoneNumber;
    @NotBlank(message = "receiverPhoneNumber를 입력해주세요.")
    String receiverPhoneNumber;
    @NotBlank(message = "zipCode를 입력해주세요.")
    String zipCode;
    @NotBlank(message = "address1를 입력해주세요.")
    String address1;
    @NotBlank(message = "address2를 입력해주세요.")
    String address2;

    public Delivery toEntity() {
        Address address = Address.builder().zipCode(zipCode)
                .address1(address1)
                .address2(address2).build();

        return Delivery.builder()
                .sender(sender)
                .receiver(receiver)
                .senderPhoneNumber(senderPhoneNumber)
                .receiverPhoneNumber(receiverPhoneNumber)
                .address(address).build();
    }
}

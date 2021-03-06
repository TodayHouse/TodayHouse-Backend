package com.todayhouse.domain.order.dto.request;

import com.todayhouse.domain.order.domain.Address;
import com.todayhouse.domain.order.domain.Delivery;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DeliverySaveRequest {
    @NotBlank(message = "sender를 입력해주세요.")
    private String sender;
    @NotBlank(message = "receiver를 입력해주세요.")
    private String receiver;
    @Email(message = "이메일을 입력해주세요.")
    private String senderEmail;
    @NotBlank(message = "senderPhoneNumber를 입력해주세요.")
    private String senderPhoneNumber;
    @NotBlank(message = "receiverPhoneNumber를 입력해주세요.")
    private String receiverPhoneNumber;
    @NotBlank(message = "zipCode를 입력해주세요.")
    private String zipCode;
    @NotBlank(message = "address1를 입력해주세요.")
    private String address1;
    @NotBlank(message = "address2를 입력해주세요.")
    private String address2;

    public Delivery toEntity() {
        Address address = Address.builder().zipCode(zipCode)
                .address1(address1)
                .address2(address2).build();

        return Delivery.builder()
                .sender(sender)
                .receiver(receiver)
                .senderEmail(senderEmail)
                .senderPhoneNumber(senderPhoneNumber)
                .receiverPhoneNumber(receiverPhoneNumber)
                .address(address).build();
    }
}

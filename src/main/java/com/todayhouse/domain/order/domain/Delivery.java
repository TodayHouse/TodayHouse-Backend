package com.todayhouse.domain.order.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_address_id")
    Long id;
    String sender;
    String receiver;

    @Column(name = "sender_phone_number")
    String senderPhoneNumber;

    @Column(name = "receiver_phone_number")
    String receiverPhoneNumber;

    @Embedded
    Address address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    Order order;
}

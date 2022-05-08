package com.todayhouse.domain.order.domain;

import lombok.*;

import javax.persistence.Embeddable;

@Getter
@Builder
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Address {
    String zipCode;
    String address1;
    String address2;
}

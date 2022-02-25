package com.todayhouse.domain.user.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_id")
    private Long id;

    @Column(name = "company_name")
    private String companyName;

    private String representative;

    @Column(name = "customer_center")
    private String customerCenter;

    private String email;

    @Column(name = "registration_num")
    private String registrationNum;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}

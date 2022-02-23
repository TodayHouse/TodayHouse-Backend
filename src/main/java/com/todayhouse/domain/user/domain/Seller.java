package com.todayhouse.domain.user.domain;

import com.todayhouse.domain.product.domain.Product;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "company_name", unique = true)
    private String companyName;

    @Column(name = "brand_name", unique = true)
    private String brandName;

    private String representative;

    @Column(name = "customer_center")
    private String customerCenter;

    private String email;

    @Column(name = "registration_num")
    private String registrationNum;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Product> products = new ArrayList<>();

}

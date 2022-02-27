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

    private String email;

    @Column(name = "brand_name", unique = true)
    private String brand;

    @Column(name = "company_name", unique = true)
    private String companyName;

    private String representative;

    @Column(name = "customer_center")
    private String customerCenter;

    @Column(name = "registration_num")
    private String registrationNum;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    @Override
    public String toString() {
        return "Seller{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", brandName='" + brand + '\'' +
                ", representative='" + representative + '\'' +
                ", customerCenter='" + customerCenter + '\'' +
                ", email='" + email + '\'' +
                ", registrationNum='" + registrationNum + '\'' +
                '}';
    }
}
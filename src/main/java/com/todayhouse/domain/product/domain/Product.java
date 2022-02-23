package com.todayhouse.domain.product.domain;

import com.todayhouse.domain.user.domain.Seller;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String image;

    private int price;

    @Column(name = "discount_rate")
    private int discountRate;

    @Column(name = "delivery_fee")
    private int deliveryFee;

    @Column(name = "special_price")
    private boolean specialPrice;

    @Column(name = "product_detail")
    private String productDetail;

    private int sales;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id")
    Seller seller;

    @Builder
    public Product(String title, String image, int price, int discountRate, int deliveryFee,
                   boolean specialPrice, String productDetail, int sales, Seller seller) {
        this.title = title;
        this.image = image;
        this.price = price;
        this.discountRate = discountRate;
        this.deliveryFee = deliveryFee;
        this.specialPrice = specialPrice;
        this.productDetail = productDetail;
        this.sales = sales;
        setSeller(seller);
    }

    public void setSeller(Seller seller) {
        if (this.seller != null) return;
        this.seller = seller;
        seller.getProducts().add(this);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", price=" + price +
                ", discountRate=" + discountRate +
                ", deliveryFee=" + deliveryFee +
                ", specialPrice=" + specialPrice +
                ", productDetail='" + productDetail + '\'' +
                ", sales=" + sales +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                ", sellerId=" + seller.getId() +
                '}';
    }
}

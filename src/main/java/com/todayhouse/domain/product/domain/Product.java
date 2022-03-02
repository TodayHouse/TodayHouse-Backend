package com.todayhouse.domain.product.domain;

import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.product.dto.request.ProductUpdateRequest;
import com.todayhouse.domain.product.exception.SellerNotSettingException;
import com.todayhouse.domain.user.domain.Seller;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;

    private String title;

    private String image;

    private int price;

    @Column(name = "delivery_fee")
    private int deliveryFee;

    @Column(name = "discount_rate")
    private int discountRate;

    @Column(name = "special_price")
    private boolean specialPrice;

    @Column(name = "product_detail")
    private String productDetail;

    private int sales;

    private String option1;

    private String option2;

    private String optional;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seller_id")
    private Seller seller;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Option> options;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Optional> optionals;

    @Builder
    public Product(String title, String image, int price, int discountRate, int deliveryFee, boolean specialPrice,
                   String productDetail, int sales, Seller seller, Category category) {
        this.title = title;
        this.image = image;
        this.price = price;
        this.deliveryFee = deliveryFee;
        this.discountRate = discountRate;
        this.specialPrice = specialPrice;
        this.productDetail = productDetail;
        this.sales = sales;
        this.category = category;
        setSeller(seller);
    }

    public void setSeller(Seller seller) {
        if (seller == null)
            throw new SellerNotSettingException();
        if (this.seller != null) return;
        this.seller = seller;
        this.brand = seller.getBrand();
        seller.getProducts().add(this);
    }

    public void updateProduct(ProductUpdateRequest request, Category category) {
        this.title = request.getTitle();
        this.image = request.getImage();
        this.price = request.getPrice();
        this.discountRate = request.getDiscountRate();
        this.deliveryFee = request.getDeliveryFee();
        this.specialPrice = request.isSpecialPrice();
        this.productDetail = request.getProductDetail();
        this.sales = request.getSales();
        this.category = category;
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

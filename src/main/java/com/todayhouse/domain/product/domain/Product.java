package com.todayhouse.domain.product.domain;

import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.likes.domain.LikesProduct;
import com.todayhouse.domain.product.dto.request.ProductUpdateRequest;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.global.common.BaseTimeEntity;
import lombok.*;
import org.hibernate.annotations.Formula;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Product extends BaseTimeEntity {
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

    private String parentOption;

    private String childOption;

    private String selectionOption;

    @Formula(
            ("select count(1) from likes l " +
            "where l.product_id = id")
    )
    @Basic(fetch = FetchType.LAZY)
    private Integer likesCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ParentOption> parents = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SelectionOption> selectionOptions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LikesProduct> likesProducts = new HashSet<>();

    @Builder
    public Product(String title, String image, int price, int discountRate, int deliveryFee, boolean specialPrice,
                   String productDetail, int sales, Seller seller, Category category, String parentOption, String childOption, String selectionOption) {
        this.title = title;
        this.image = image;
        this.price = price;
        this.deliveryFee = deliveryFee;
        this.discountRate = discountRate;
        this.specialPrice = specialPrice;
        this.productDetail = productDetail;
        this.sales = sales;
        this.category = category;
        this.parentOption = parentOption;
        this.childOption = childOption;
        this.selectionOption = selectionOption;
        setSeller(seller);
    }

    public void setSeller(Seller seller) {
        if (seller == null) return;
        this.seller = seller;
        this.brand = seller.getBrand();
    }

    public void update(ProductUpdateRequest request, Category category) {
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

    public void updateImage(String image) {
        this.image = image;
    }

    public Integer getLikesCount() {
        return getLikesProducts().size();
    }
}

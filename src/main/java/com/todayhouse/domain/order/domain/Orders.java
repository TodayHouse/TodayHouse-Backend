package com.todayhouse.domain.order.domain;

import com.todayhouse.domain.product.domain.ChildOption;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.domain.SelectionOption;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "Orders") // order는 db예약어
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orders extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    private int totalPrice;

    @Column(name = "delivery_fee")
    private int deliveryFee;

    private int productQuantity = 0;

    private int selectionQuantity = 0;

    private String memo;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_option_id")
    private ParentOption parentOption;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_option_id")
    private ChildOption childOption;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selection_option_id")
    private SelectionOption selectionOption;

    @Builder
    public Orders(String memo, User user, Product product,
                  ParentOption parentOption, ChildOption childOption, SelectionOption selectionOption,
                  int productQuantity, int selectionQuantity) {
        this.memo = memo;
        this.status = Status.PROCESSING;
        this.user = user;
        this.product = product;
        this.parentOption = parentOption;
        this.childOption = childOption;
        this.selectionOption = selectionOption;
        this.productQuantity = productQuantity;
        this.selectionQuantity = selectionQuantity;
        this.totalPrice = (childOption == null ? parentOption.getPrice() : childOption.getPrice()) * productQuantity +
                (selectionOption == null ? 0 : selectionOption.getPrice() * selectionQuantity);
    }

    public void updateStatus(Status status) {
        this.status = status;
    }
}

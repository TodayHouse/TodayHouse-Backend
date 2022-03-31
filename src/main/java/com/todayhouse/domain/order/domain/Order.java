package com.todayhouse.domain.order.domain;

import com.todayhouse.domain.product.domain.ChildOption;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.product.domain.SelectionOption;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "Orders") // order는 db예약어
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    Long id;

    int quantity;

    String memo;

    @Enumerated(EnumType.STRING)
    Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_option_id")
    ParentOption parentOption;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_option_id")
    ChildOption childOption;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selection_option_id")
    SelectionOption selectionOption;

    @Builder
    public Order(int quantity, String memo, User user, Product product,
                 ParentOption parentOption, ChildOption childOption, SelectionOption selectionOption) {
        this.memo = memo;
        this.quantity = quantity;
        this.status = Status.PROCESSING;
        this.user = user;
        this.product = product;
        this.parentOption = parentOption;
        this.childOption = childOption;
        this.selectionOption = selectionOption;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }
}

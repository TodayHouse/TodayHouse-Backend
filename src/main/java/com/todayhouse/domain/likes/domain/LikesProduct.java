package com.todayhouse.domain.likes.domain;

import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.user.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@NoArgsConstructor
@Getter
public class LikesProduct extends Likes {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public LikesProduct(User user, Product product) {
        super(user);
        this.product = product;
    }
}

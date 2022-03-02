package com.todayhouse.domain.product.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Optional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private int price;

    private int stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    public Optional(String content, int price, int stock, Product product) {
        this.content = content;
        this.price = price;
        this.stock = stock;
        setProduct(product);
    }

    public void setProduct(Product product) {
        if (this.product != null || product == null) return;
        this.product = product;
        product.getOptionals().add(this);
    }
}

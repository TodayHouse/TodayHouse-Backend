package com.todayhouse.domain.product.domain;

import com.todayhouse.domain.product.exception.OptionExistException;
import com.todayhouse.domain.product.exception.ProductExistException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private int price;

    private int stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Option parent;

    @OneToMany(mappedBy = "parent")
    private List<Option> children = new LinkedList<>();

    @Builder
    public Option(String content, int price, int stock, Product product, Option parent) {
        this.content = content;
        this.price = price;
        this.stock = stock;
        setProduct(product);
        setParent(parent);
    }

    public void setParent(Option option) {
        if (parent != null)
            throw new OptionExistException();

        if (option == null) return;
        this.parent = option;
        option.getChildren().add(this);
    }

    public void setProduct(Product product) {
        if (this.product != null)
            throw new ProductExistException();

        if (product == null) return;
        this.product = product;
        product.getOptions().add(this);
    }
}

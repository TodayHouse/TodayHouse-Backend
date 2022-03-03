package com.todayhouse.domain.product.domain;

import com.todayhouse.domain.product.exception.OptionExistException;
import com.todayhouse.domain.product.exception.StockNotEnoughException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChildOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    private String content;

    private int price;

    private int stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ParentOption parent;

    @Builder
    public ChildOption(String content, int price, int stock, Product product, ParentOption parent) {
        this.content = content;
        this.price = price;
        this.stock = stock;
        setParent(parent);
    }

    public void setParent(ParentOption option) {
        if (parent != null)
            throw new OptionExistException();

        if (option == null) return;
        this.parent = option;
        option.getChildren().add(this);
    }

    public void addStock(int count) {
        if (stock - count < 0)
            throw new StockNotEnoughException();

        stock += count;
    }

    public void changePrice(int price) {
        this.price = price;
    }

    public void changeContent(String content) {
        this.content = content;
    }
}

package com.todayhouse.domain.product.domain;

import com.todayhouse.domain.product.dto.request.ChildOptionUpdateRequest;
import com.todayhouse.domain.product.exception.OptionExistException;
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
    public ChildOption(String content, int price, int stock, ParentOption parent) {
        this.price = price;
        this.stock = stock;
        this.content = content;
        setParent(parent);
    }

    public void update(ChildOptionUpdateRequest request) {
        this.price = request.getPrice();
        this.stock = request.getStock();
        this.content = request.getContent();
    }

    public void setParent(ParentOption option) {
        if (parent != null)
            throw new OptionExistException();

        if (option == null) return;
        this.parent = option;
        option.getChildren().add(this);
    }
}

package com.todayhouse.domain.product.domain;

import com.todayhouse.domain.product.dto.request.ParentOptionUpdateRequest;
import com.todayhouse.domain.product.exception.ProductExistException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParentOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private int price;

    private int stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChildOption> children = new LinkedHashSet<>();

    @Builder
    public ParentOption(String content, int price, int stock, Product product) {
        this.price = price;
        this.stock = stock;
        this.content = content;
        setProduct(product);
    }

    public void update(ParentOptionUpdateRequest request) {
        this.price = request.getPrice();
        this.stock = request.getStock();
        this.content = request.getContent();
    }

    public void setProduct(Product product) {
        if (this.product != null)
            throw new ProductExistException();

        if (product == null) return;
        this.product = product;
        product.getParents().add(this);
    }
}

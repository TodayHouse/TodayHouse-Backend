package com.todayhouse.domain.product.domain;

import com.todayhouse.domain.product.dto.request.SelectionOptionUpdateRequest;
import com.todayhouse.domain.product.exception.ProductExistException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SelectionOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int price;

    private int stock;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    public SelectionOption(String content, int price, int stock, Product product) {
        this.price = price;
        this.stock = stock;
        this.content = content;
        setProduct(product);
    }

    public void setProduct(Product product) {
        if (this.product != null)
            throw new ProductExistException();

        if (product == null) return;
        this.product = product;
        product.getSelectionOptions().add(this);
    }

    public void update(SelectionOptionUpdateRequest request){
        this.price = request.getPrice();
        this.stock = request.getStock();
        this.content = request.getContent();
    }
}

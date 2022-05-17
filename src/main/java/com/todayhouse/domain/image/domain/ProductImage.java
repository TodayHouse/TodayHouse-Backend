package com.todayhouse.domain.image.domain;

import com.todayhouse.domain.product.domain.Product;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("product")
public class ProductImage extends Image {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder
    public ProductImage(String fileName, Product product) {
        super(fileName);
        this.product = product;
    }
}

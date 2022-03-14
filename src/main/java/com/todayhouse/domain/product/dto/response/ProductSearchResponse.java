package com.todayhouse.domain.product.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductSearchResponse {
    List<ProductResponse> content;
    long totalElements;
    long totalPages;
    long size;
    boolean empty;

    public ProductSearchResponse(Page<ProductResponse> pages) {
        this.content = pages.getContent();
        this.totalElements = pages.getTotalElements();
        this.totalPages = pages.getTotalPages();
        this.size = pages.getSize();
        this.empty = pages.isEmpty();
    }
}

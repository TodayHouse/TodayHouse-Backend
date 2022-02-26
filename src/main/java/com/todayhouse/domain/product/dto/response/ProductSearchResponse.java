package com.todayhouse.domain.product.dto.response;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductSearchResponse {
    List<ProductResponse> content;
    long totalElements;
    long totalPages;
    long size;
    boolean empty;

    public ProductSearchResponse (Page<ProductResponse> pages){
        this.content = pages.getContent();
        this.totalElements = pages.getTotalElements();
        this.totalPages = pages.getTotalPages();
        this.size = pages.getSize();
        this.empty = pages.isEmpty();
    }
}

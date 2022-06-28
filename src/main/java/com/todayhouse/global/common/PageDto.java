package com.todayhouse.global.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageDto<T> {
    List<T> content;
    long size;
    long totalPages;
    long totalElements;
    boolean last;


    public PageDto(Page<T> page) {
        this.content = page.getContent();
        this.size = page.getSize();
        this.last = page.isLast();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
    }
}

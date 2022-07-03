package com.todayhouse.domain.scrap.application;

import com.todayhouse.domain.scrap.domain.Scrap;

public interface ScrapService {
    Scrap saveScrap(Long productId);

    Boolean isScraped(Long productId);

    void deleteScrap(Long productId);

    Long countScrapByProductId(Long productId);

    Long countMyScrap();
}

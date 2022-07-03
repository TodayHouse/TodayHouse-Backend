package com.todayhouse.domain.scrap.application;

import com.todayhouse.domain.scrap.domain.Scrap;

public interface ScrapService {
    Scrap saveScrap(Long storyId);

    Boolean isScraped(Long storyId);

    void deleteScrap(Long storyId);

    Long countScrapByStoryId(Long storyId);

    Long countMyScrap();
}

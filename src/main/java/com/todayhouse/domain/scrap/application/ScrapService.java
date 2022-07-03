package com.todayhouse.domain.scrap.application;

import com.todayhouse.domain.scrap.domain.Scrap;
import com.todayhouse.domain.story.domain.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScrapService {
    Scrap saveScrap(Long storyId);

    Boolean isScraped(Long storyId);

    void deleteScrap(Long storyId);

    Long countScrapByStoryId(Long storyId);

    Long countMyScrap();

    Page<Story> findScrapedStories(Pageable pageable);
}

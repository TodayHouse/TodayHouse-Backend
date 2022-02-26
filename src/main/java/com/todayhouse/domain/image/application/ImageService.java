package com.todayhouse.domain.image.application;

import com.todayhouse.domain.story.domain.Story;

import java.util.List;

public interface ImageService {
    void save(List<String> fileName, Story story);
}

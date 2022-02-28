package com.todayhouse.domain.image.application;

import com.todayhouse.domain.story.domain.Story;

import java.util.List;

public interface ImageService {
    void save(List<String> fileName, Story story);

    byte[] getImage(String fileName);

    void deleteOne(String fileName);

    void delete(List<String> fileName);

    String getThumbnailUrl(Story story);

    List<String> findAll();
}

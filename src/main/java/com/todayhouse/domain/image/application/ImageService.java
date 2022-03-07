package com.todayhouse.domain.image.application;

import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.story.domain.Story;

import java.util.List;

public interface ImageService {
    void save(List<String> fileName, Story story);

    void save(List<String> fileName, Product product);

    byte[] getImage(String fileName);

    void deleteOne(String fileName);

    void delete(List<String> fileName);

    String findThumbnailUrl(Story story);

    String findThumbnailUrl(Product product);

    List<String> findStoryImageAll();

    List<String> findProductImageAll();
}

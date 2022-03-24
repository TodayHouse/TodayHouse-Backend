package com.todayhouse.domain.image.application;

import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.story.domain.Story;

import java.util.List;

public interface ImageService {
    void save(List<String> fileName, Story story);

    void saveOne(String fileName, Story story);

    void save(List<String> fileName, Product product);

    void saveOne(String fileName, Product product);

    void deleteStoryImages(List<String> fileNames);

    void deleteProductImages(List<String> fileNames);

    String findThumbnailUrl(Story story);

    String findThumbnailUrl(Product product);

    List<String> findStoryImageFileNamesAll();

    List<String> findProductImageFileNamesByProductId(Long productId);
}

package com.todayhouse.domain.image.application;

import com.todayhouse.domain.image.dao.ProductImageRepository;
import com.todayhouse.domain.image.dao.StoryImageRepository;
import com.todayhouse.domain.image.domain.ProductImage;
import com.todayhouse.domain.image.domain.StoryImage;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.infra.S3Storage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final FileService fileService;
    private final StoryImageRepository storyImageRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    public void save(List<String> fileNames, Story story) {
        storyImageRepository.saveAll(fileNames
                .stream()
                .map(file -> new StoryImage(file, story))
                .collect(Collectors.toList()));
    }

    @Override
    public void save(List<String> fileNames, Product product) {
        productImageRepository.saveAll(fileNames
                .stream()
                .map(file -> new ProductImage(file, product))
                .collect(Collectors.toList()));
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getImage(String fileName) {
        return fileService.getImage(fileName);
    }

    @Override
    public void deleteStoryImages(List<String> fileNames) {
        for (String fileName : fileNames)
            storyImageRepository.deleteByFileName(fileName);
        fileService.delete(fileNames);
    }

    @Override
    public void deleteProductImages(List<String> fileNames) {
        for (String fileName : fileNames)
            productImageRepository.deleteByFileName(fileName);
        fileService.delete(fileNames);
    }

    @Override
    @Transactional(readOnly = true)
    public String findThumbnailUrl(Story story) {
        StoryImage image = storyImageRepository.findFirstByStoryOrderByCreatedAtDesc(story).orElse(null);
        if (image == null) return null;
        return image.getFileName();
    }

    @Override
    @Transactional(readOnly = true)
    public String findThumbnailUrl(Product product) {
        ProductImage image = productImageRepository.findFirstByProductOrderByCreatedAtDesc(product).orElse(null);
        if (image == null) return null;
        return image.getFileName();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findStoryImageFileNamesAll() {
        return storyImageRepository.findAll().stream()
                .map(image -> image.getFileName())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findProductImageFileNamesAll() {
        return productImageRepository.findAll().stream()
                .map(image -> image.getFileName())
                .collect(Collectors.toList());
    }
}

package com.todayhouse.domain.image.application;

import com.todayhouse.domain.image.dao.ProductImageRepository;
import com.todayhouse.domain.image.dao.StoryImageRepository;
import com.todayhouse.domain.image.domain.ProductImage;
import com.todayhouse.domain.image.domain.StoryImage;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.global.error.BaseException;
import com.todayhouse.global.error.BaseResponseStatus;
import com.todayhouse.infra.S3Storage.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    public void save(List<String> fileName, Story story) {
        storyImageRepository.saveAll(fileName
                .stream()
                .map(file -> new StoryImage(file, story))
                .collect(Collectors.toList()));
    }

    @Override
    public void save(List<String> fileName, Product product) {
        productImageRepository.saveAll(fileName
                .stream()
                .map(file -> new ProductImage(file, product))
                .collect(Collectors.toList()));
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getImage(String fileName) {
        try {
            return fileService.getImage(fileName);
        } catch (IOException e) {
            throw new BaseException(BaseResponseStatus.IMAGE_FILE_IO_EXCEPTION);
        }
    }

    @Override
    public void deleteOne(String fileName) {
        fileService.deleteOne(fileName);
    }

    @Override
    public void delete(List<String> fileName) {
        fileService.delete(fileName);
    }

    @Override
    @Transactional(readOnly = true)
    public String findThumbnailUrl(Story story) {
        StoryImage image = storyImageRepository.findFirstByStoryOrderByCreatedAtDesc(story).orElseGet(null);
        if (image == null) return null;
        return image.getFileName();
    }

    @Override
    @Transactional(readOnly = true)
    public String findThumbnailUrl(Product product) {
        ProductImage image = productImageRepository.findFirstByProductOrderByCreatedAtDesc(product).orElseGet(null);
        if (image == null) return null;
        return image.getFileName();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findStoryImageAll() {
        return storyImageRepository.findAll().stream()
                .map(image -> image.getFileName())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findProductImageAll() {
        return productImageRepository.findAll().stream()
                .map(image -> image.getFileName())
                .collect(Collectors.toList());
    }
}

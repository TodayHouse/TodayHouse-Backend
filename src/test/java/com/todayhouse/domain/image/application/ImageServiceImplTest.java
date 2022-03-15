package com.todayhouse.domain.image.application;

import com.todayhouse.domain.image.dao.ProductImageRepository;
import com.todayhouse.domain.image.dao.StoryImageRepository;
import com.todayhouse.domain.image.domain.ProductImage;
import com.todayhouse.domain.image.domain.StoryImage;
import com.todayhouse.domain.product.domain.Product;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.user.domain.Seller;
import com.todayhouse.infra.S3Storage.service.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @InjectMocks
    ImageServiceImpl imageService;

    @Mock
    FileService fileService;

    @Mock
    StoryImageRepository storyImageRepository;

    @Mock
    ProductImageRepository productImageRepository;

    @Test
    @DisplayName("story image file 이름 저장")
    void save() {
        List<String> files = List.of("file1", "file2", "file3");
        Story story = Mockito.mock(Story.class);
        List<StoryImage> lists = files.stream().map(file -> new StoryImage(file, story))
                .collect(Collectors.toList());
        when(storyImageRepository.saveAll(anyList())).thenReturn(anyList());
        imageService.save(files, story);
    }

    @Test
    @DisplayName("product image file 이름 저장")
    void testSave() {
        List<String> files = List.of("file1", "file2", "file3");
        Product product = Product.builder().title("test").seller(Mockito.mock(Seller.class)).build();
        List<ProductImage> lists = files.stream().map(file -> new ProductImage(file, product))
                .collect(Collectors.toList());
        when(productImageRepository.saveAll(anyList())).thenReturn(anyList());
        imageService.save(files, product);
    }

    @Test
    @DisplayName("Story filename 찾기")
    void findThumbnailUrl() {
        Story story = Mockito.mock(Story.class);
        StoryImage test = StoryImage.builder().fileName("test").build();
        when(storyImageRepository.findFirstByStoryOrderByCreatedAtDesc(any(Story.class))).thenReturn(Optional.ofNullable(test));

        assertThat(imageService.findThumbnailUrl(story)).isEqualTo(test.getFileName());
    }

    @Test
    @DisplayName("Product filename 찾기")
    void testFindThumbnailUrl() {
        Product product = Mockito.mock(Product.class);
        ProductImage test = ProductImage.builder().fileName("test").product(product).build();
        when(productImageRepository.findFirstByProductOrderByCreatedAtAsc(any(Product.class))).thenReturn(Optional.ofNullable(test));

        assertThat(imageService.findThumbnailUrl(product)).isEqualTo(test.getFileName());
    }

    @Test
    @DisplayName("Story filename이 null")
    void findStoryThumbnailUrlNull() {
        Story story = Mockito.mock(Story.class);
        when(storyImageRepository.findFirstByStoryOrderByCreatedAtDesc(any(Story.class))).thenReturn(Optional.ofNullable(null));

        assertThat(imageService.findThumbnailUrl(story)).isEqualTo(null);
    }

    @Test
    @DisplayName("Product filename이 null")
    void findProductThumbnailUrlNull() {
        Product product = Mockito.mock(Product.class);
        when(productImageRepository.findFirstByProductOrderByCreatedAtAsc(any(Product.class))).thenReturn(Optional.ofNullable(null));

        assertThat(imageService.findThumbnailUrl(product)).isEqualTo(null);
    }

    @Test
    @DisplayName("모든 story image 찾기")
    void findStoryImageAll() {
        StoryImage a = StoryImage.builder().fileName("a").build();
        StoryImage b = StoryImage.builder().fileName("b").build();
        List<StoryImage> list = List.of(a, b);
        when(storyImageRepository.findAll()).thenReturn(list);

        List<String> result = imageService.findStoryImageFileNamesAll();

        assertThat(result.size()).isEqualTo(2);
        assertTrue(result.contains("a"));
        assertTrue(result.contains("b"));
    }

    @Test
    @DisplayName("모든 product image 찾기")
    void findProductImageAll() {
        Product product = Mockito.mock(Product.class);
        ProductImage a = ProductImage.builder().fileName("a").product(product).build();
        ProductImage b = ProductImage.builder().fileName("b").product(product).build();
        List<ProductImage> list = List.of(a, b);
        when(productImageRepository.findAll()).thenReturn(list);

        List<String> result = imageService.findProductImageFileNamesAll();

        assertThat(result.size()).isEqualTo(2);
        assertTrue(result.contains("a"));
        assertTrue(result.contains("b"));
    }

    @Test
    @DisplayName("Story image 리스트로 삭제")
    void deleteStoryImages() {
        List<String> list = List.of("a", "b", "c");
        doNothing().when(storyImageRepository).deleteByFileName(anyString());
        doNothing().when(fileService).delete(anyList());

        imageService.deleteStoryImages(list);
    }

    @Test
    @DisplayName("Product image 리스트로 삭제")
    void deleteProductImages() {
        List<String> list = List.of("a", "b", "c");
        Product product = Product.builder().image("oldImg.jpg").seller(Mockito.mock(Seller.class)).build();

        when(productImageRepository.findByFileName(anyString()))
                .thenReturn(Optional.ofNullable(ProductImage.builder().product(product).fileName("oldImg.jpg").build()));
        doNothing().when(productImageRepository).deleteByFileName(anyString());
        when(productImageRepository.findFirstByProductOrderByCreatedAtAsc(any(Product.class)))
                .thenReturn(Optional.ofNullable(ProductImage.builder().product(product).fileName("newImg.jpg").build()));
        doNothing().when(fileService).delete(anyList());

        imageService.deleteProductImages(list);

        assertThat(product.getImage()).isEqualTo("newImg.jpg");
    }

}
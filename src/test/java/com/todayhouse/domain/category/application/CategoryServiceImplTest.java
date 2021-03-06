package com.todayhouse.domain.category.application;

import com.todayhouse.domain.category.dao.CategoryRepository;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.category.dto.request.CategorySaveRequest;
import com.todayhouse.domain.category.dto.request.CategoryUpdateRequest;
import com.todayhouse.domain.category.exception.CategoryExistException;
import com.todayhouse.domain.category.exception.CategoryNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @InjectMocks
    CategoryServiceImpl categoryService;

    @Mock
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("새 카테고리 저장")
    void addCategory() {
        CategorySaveRequest request = CategorySaveRequest.builder().name("c1").build();
        Category category = Category.builder().name("c1").build();

        when(categoryRepository.existsByName("c1")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        assertThat(categoryService.addCategory(request)).isEqualTo(category);
    }

    @Test
    @DisplayName("새 카테고리 이름 중복 예외처리")
    void addCategoryException() {
        CategorySaveRequest request = CategorySaveRequest.builder().name("c1").build();
        when(categoryRepository.existsByName(anyString())).thenReturn(true);
        assertThrows(CategoryExistException.class, () -> categoryService.addCategory(request));
    }

    @Test
    void 잘못된_부모_카테고리() {
        CategorySaveRequest request = CategorySaveRequest.builder().name("c1").parentName("par").build();

        when(categoryRepository.existsByName("c1")).thenReturn(false);
        when(categoryRepository.findByName("par")).thenReturn(Optional.ofNullable(null));

        assertThrows(CategoryNotFoundException.class, () -> categoryService.addCategory(request));
    }

    @Test
    @DisplayName("category 업데이트")
    void updateCategory() {
        CategoryUpdateRequest request = CategoryUpdateRequest.builder().name("old").changeName("new").build();
        Category old = Category.builder().name("old").build();
        when(categoryRepository.findByName("old")).thenReturn(Optional.ofNullable(old));

        Category category = categoryService.updateCategory(request);
        assertThat(category.getName()).isEqualTo("new");
    }

    @Test
    @DisplayName("category 삭제")
    void deleteCategory() {
        doNothing().when(categoryRepository).deleteByName(anyString());

        categoryService.deleteCategory("old");

        verify(categoryRepository).deleteByName(anyString());
    }

    @Test
    @DisplayName("모든 category 찾기")
    void findAll() {
        Category c1 = Category.builder().name("c1").build();
        Category c2 = Category.builder().name("c2").build();
        Category c3 = Category.builder().name("c3").parent(c2).build();
        Category c4 = Category.builder().name("c4").parent(c3).build();
        ReflectionTestUtils.setField(c1, "id", 1L);
        ReflectionTestUtils.setField(c2, "id", 2L);
        ReflectionTestUtils.setField(c3, "id", 3L);
        ReflectionTestUtils.setField(c4, "id", 4L);

        List<Category> list = List.of(c1, c2, c3, c4);

        when(categoryRepository.findAllByOrderByDepthAscParentAscIdAsc()).thenReturn(list);

        List<Category> categories = categoryService.findAllWithChildrenAll();
        assertThat(categories).isEqualTo(list);
    }

    @Test
    void 이름으로_특정_카테고리_찾기() {
        Category c1 = Category.builder().name("c1").build();
        Category c2 = Category.builder().name("c2").build();
        Category c3 = Category.builder().name("c3").parent(c2).build();
        ReflectionTestUtils.setField(c1, "id", 1L);
        ReflectionTestUtils.setField(c2, "id", 2L);
        ReflectionTestUtils.setField(c3, "id", 3L);

        List<Category> list = List.of(c2, c3);

        when(categoryRepository.findOneByNameWithAllChildren("c2")).thenReturn(list);

        createCategoryResponse(list);
        List<Category> categories = categoryService.findOneByNameWithChildrenAll("c2");
        assertThat(categories).isEqualTo(list);
    }

    @Test
    @DisplayName("해당 이름의 카테고리 없음")
    void findOneWithChildrenAllByNameException() {
        when(categoryRepository.findOneByNameWithAllChildren("??")).thenReturn(List.of());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.findOneByNameWithChildrenAll("??"));
    }

    @Test
    @DisplayName("카테고리 경로 리스트로 찾기")
    void findRootPath() {
        when(categoryRepository.findRootPathByName(anyString()))
                .thenReturn(List.of(Mockito.mock(Category.class), Mockito.mock(Category.class)));

        List<Category> categories = categoryService.findRootPath("c");
        assertThat(categories.size()).isEqualTo(2);
    }

    private void createCategoryResponse(List<Category> categories) {
    }
}
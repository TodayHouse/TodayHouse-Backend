package com.todayhouse.domain.category.application;

import com.todayhouse.domain.category.dao.CategoryRepository;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.category.dto.request.CategorySaveRequest;
import com.todayhouse.domain.category.dto.request.CategoryUpdateRequest;
import com.todayhouse.domain.category.dto.response.CategoryResponse;
import com.todayhouse.domain.category.exception.CategoryNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void 잘못된_부모_카테고리() {
        CategorySaveRequest request = CategorySaveRequest.builder().name("c1").parentName("p1").build();

        when(categoryRepository.existsByName("c1")).thenReturn(false);
        when(categoryRepository.findByName("p1")).thenReturn(Optional.ofNullable(null));

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
        String name = "c";
        doNothing().when(categoryRepository).deleteByName(name);

        categoryService.deleteCategory(name);

        verify(categoryRepository).deleteByName(name);
    }

    @Test
    @DisplayName("모든 category 찾기")
    void findAll() {
        Category c1 = Category.builder().name("c1").build();
        Category c2 = Category.builder().name("c2").build();
        Category c3 = Category.builder().name("c3").parent(c2).build();
        List<Category> list = new ArrayList<>();
        list.add(c1);
        list.add(c2);
        list.add(c3);

        when(categoryRepository.findByDepth(0)).thenReturn(list);

        List<CategoryResponse> find = categoryService.findAll();
        assertTrue(find.stream().anyMatch(c -> c.getName().equals("c1")));
        assertTrue(find.stream().anyMatch(c -> c.getName().equals("c2") &&
                c.getSubCategory().get(0).getName().equals("c3")));
    }

    @Test
    void 특정_카테고리_찾기() {
        Category c1 = Category.builder().name("c1").build();
        Category c2 = Category.builder().name("c2").build();
        Category c3 = Category.builder().name("c3").parent(c2).build();
        List<Category> list = new ArrayList<>();
        list.add(c1);
        list.add(c2);
        list.add(c3);

        when(categoryRepository.findByName("c2")).thenReturn(Optional.ofNullable(c2));

        CategoryResponse find = categoryService.findAllByName("c2");
        assertThat(find.getName()).isEqualTo("c2");
        assertThat(find.getSubCategory().get(0).getName()).isEqualTo("c3");
    }
}
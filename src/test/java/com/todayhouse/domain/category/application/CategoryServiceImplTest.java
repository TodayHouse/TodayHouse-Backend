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
import org.springframework.test.util.ReflectionTestUtils;

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
        CategorySaveRequest request = CategorySaveRequest.builder().name("c1").parentId(1L).build();

        when(categoryRepository.existsByName("c1")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        assertThrows(CategoryNotFoundException.class, () -> categoryService.addCategory(request));
    }

    @Test
    @DisplayName("category 업데이트")
    void updateCategory() {
        CategoryUpdateRequest request = CategoryUpdateRequest.builder().id(1L).changeName("new").build();
        Category old = Category.builder().name("old").build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.ofNullable(old));

        Category category = categoryService.updateCategory(request);
        assertThat(category.getName()).isEqualTo("new");
    }

    @Test
    @DisplayName("category 삭제")
    void deleteCategory() {
        doNothing().when(categoryRepository).deleteById(anyLong());

        categoryService.deleteCategory(1L);

        verify(categoryRepository).deleteById(anyLong());
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

        List<Category> find = categoryService.findAllWithChildrenAll();
        assertTrue(find.stream().anyMatch(c -> c.getName().equals("c1")));
        assertTrue(find.stream().anyMatch(c -> c.getName().equals("c2") &&
                c.getChildren().get(0).getName().equals("c3")));
    }

    @Test
    void 특정_카테고리_찾기() {
        Category c1 = Category.builder().name("c1").build();
        Category c2 = Category.builder().name("c2").build();
        Category c3 = Category.builder().name("c3").parent(c2).build();
        ReflectionTestUtils.setField(c1, "id", 1L);
        ReflectionTestUtils.setField(c2, "id", 2L);
        ReflectionTestUtils.setField(c3, "id", 3L);

        List<Category> list = new ArrayList<>();
        list.add(c2);
        list.add(c3);

        when(categoryRepository.findById(2L)).thenReturn(Optional.ofNullable(c2));
        createCategoryResponse(list);
        CategoryResponse find = categoryService.findOneWithChildrenAllById(2L);
        assertThat(find.getName()).isEqualTo("c2");
        assertThat(find.getSubCategories().get(0).getName()).isEqualTo("c3");
    }

    @Test
    @DisplayName("해당 id의 카테고리 없음")
    void findOneWithChildrenAllByIdException() {
        when(categoryRepository.findById(2L)).thenReturn(Optional.ofNullable(null));

        assertThrows(CategoryNotFoundException.class, () -> categoryService.findOneWithChildrenAllById(2L));
    }

    private void createCategoryResponse(List<Category> categories) {
        when(categoryRepository.findOneWithAllChildrenById(anyLong())).thenReturn(categories);
    }
}